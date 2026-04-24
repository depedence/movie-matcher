const token = localStorage.getItem('token')

const state = {
    currentUserId: null,
    feedInitialized: false,
    pendingDeleteAction: null
}

function ensureAdminPageAccess() {
    if (!token) {
        window.location.href = '/login'
        return false
    }

    const payload = decodeTokenPayload(token)
    if (!payload?.sub) {
        localStorage.removeItem('token')
        window.location.href = '/login'
        return false
    }

    if (payload.role !== 'ADMIN') {
        window.location.href = '/home'
        return false
    }

    return true
}

// API
async function apiRequest(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }

    if (body) {
        options.body = JSON.stringify(body)
    }

    return await fetch(url, options)
}

// TOAST
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast')
    toast.textContent = message
    toast.className = `toast ${type}`
    setTimeout(() => toast.classList.add('hidden'), 3000)
}

// MODAL
function openModal(id) {
    document.getElementById(id).classList.remove('hidden')
}

function closeModal(id) {
    document.getElementById(id).classList.add('hidden')
}

// HELPERS
function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;')
}

function formatValue(value, fallback = '—') {
    if (value === null || value === undefined || value === '') {
        return fallback
    }
    return value
}

function formatGenres(genres) {
    if (!Array.isArray(genres) || genres.length === 0) {
        return '—'
    }

    return genres.join(', ')
}

function decodeTokenPayload(jwtToken) {
    if (!jwtToken) {
        return null
    }

    const parts = jwtToken.split('.')
    if (parts.length < 2) {
        return null
    }

    try {
        const base64 = parts[1].replaceAll('-', '+').replaceAll('_', '/')
        const normalized = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=')
        return JSON.parse(atob(normalized))
    } catch (error) {
        return null
    }
}

function getCurrentUsername() {
    return decodeTokenPayload(token)?.sub ?? null
}

async function ensureCurrentUserId(users = null) {
    if (state.currentUserId !== null) {
        return state.currentUserId
    }

    const resolvedUsers = users ?? await fetchUsers()
    const currentUsername = getCurrentUsername()
    const currentUser = resolvedUsers.find(user => user.username === currentUsername)

    if (!currentUser) {
        throw new Error('Current user not found')
    }

    state.currentUserId = currentUser.id
    return state.currentUserId
}

// USERS
async function fetchUsers() {
    const response = await apiRequest('/api/users')
    if (!response.ok) {
        throw new Error('Failed to load users')
    }
    return await response.json()
}

async function loadUsers() {
    const users = await fetchUsers()
    await ensureCurrentUserId(users)

    const tbody = document.querySelector('#users-table tbody')
    tbody.innerHTML = ''

    users.forEach(user => {
        const row = document.createElement('tr')
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${escapeHtml(user.username)}</td>
            <td>
                <button class="btn-edit" type="button">Edit</button>
                <button class="btn-delete" type="button">Delete</button>
            </td>
        `
        row.querySelector('.btn-edit').addEventListener('click', () => openEditModal(user.id, user.username))
        row.querySelector('.btn-delete').addEventListener('click', () => openDeleteModal(user.id))
        tbody.appendChild(row)
    })
}

// MOVIES
async function fetchMovies() {
    const response = await apiRequest('/api/movies')
    if (!response.ok) {
        throw new Error('Failed to load movies')
    }
    return await response.json()
}

async function deleteAllMovies() {
    const response = await apiRequest('/api/movies', 'DELETE')

    if (!response.ok) {
        throw new Error('Failed to delete movies')
    }

    return await response.json()
}

async function triggerFeed() {
    const userId = await ensureCurrentUserId()
    const response = await apiRequest(`/api/movies/feed?userId=${userId}`)

    if (!response.ok) {
        throw new Error('Failed to load feed')
    }

    state.feedInitialized = true
    return await response.json()
}

function renderMovies(movies) {
    const tbody = document.querySelector('#movies-table tbody')
    tbody.innerHTML = ''

    movies.forEach(movie => {
        const row = document.createElement('tr')
        row.innerHTML = `
            <td>${formatValue(movie.id)}</td>
            <td>${escapeHtml(formatValue(movie.title))}</td>
            <td>${formatValue(movie.releaseYear)}</td>
            <td>${formatValue(movie.rating)}</td>
            <td>${escapeHtml(formatGenres(movie.genres))}</td>
        `
        row.addEventListener('click', () => openMovieDetails(movie))
        tbody.appendChild(row)
    })
}

async function loadMovies({ forceFeed = false } = {}) {
    let movies = await fetchMovies()

    if (forceFeed) {
        await triggerFeed()
        movies = await fetchMovies()
        showToast('Feed updated')
    } else if (movies.length === 0 && !state.feedInitialized) {
        await triggerFeed()
        movies = await fetchMovies()

        if (movies.length > 0) {
            showToast('Movies loaded from feed')
        }
    }

    renderMovies(movies)

    if (movies.length === 0) {
        showToast('No movies found', 'error')
    }
}

function openMovieDetails(movie) {
    document.getElementById('movie-modal-title').textContent = formatValue(movie.title)
    document.getElementById('movie-modal-id').textContent = formatValue(movie.id)
    document.getElementById('movie-modal-imdb').textContent = formatValue(movie.imdbId)
    document.getElementById('movie-modal-year').textContent = formatValue(movie.releaseYear)
    document.getElementById('movie-modal-rating').textContent = formatValue(movie.rating)
    document.getElementById('movie-modal-description').textContent = formatValue(movie.description, 'No description')

    const genresContainer = document.getElementById('movie-modal-genres')
    genresContainer.innerHTML = ''
    if (Array.isArray(movie.genres) && movie.genres.length > 0) {
        movie.genres.forEach(genre => {
            const genreTag = document.createElement('span')
            genreTag.className = 'movie-tag'
            genreTag.textContent = genre
            genresContainer.appendChild(genreTag)
        })
    } else {
        const genreTag = document.createElement('span')
        genreTag.className = 'movie-tag'
        genreTag.textContent = 'No genres'
        genresContainer.appendChild(genreTag)
    }

    const poster = document.getElementById('movie-modal-poster')
    const posterFallback = document.getElementById('movie-modal-poster-fallback')
    if (movie.posterUrl && movie.posterUrl !== 'N/A') {
        poster.src = movie.posterUrl
        poster.alt = `${formatValue(movie.title)} poster`
        poster.classList.remove('hidden')
        posterFallback.classList.add('hidden')
    } else {
        poster.removeAttribute('src')
        poster.classList.add('hidden')
        posterFallback.classList.remove('hidden')
    }

    openModal('modal-movie-details')
}

// TABS
async function switchTab(tabName) {
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.classList.toggle('active', button.dataset.tab === tabName)
    })

    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.toggle('active', content.id === `tab-${tabName}`)
    })

    if (tabName === 'movies') {
        await loadMovies()
    }
}

document.querySelectorAll('.tab-btn').forEach(button => {
    button.addEventListener('click', async () => {
        try {
            await switchTab(button.dataset.tab)
        } catch (error) {
            showToast('Error loading tab data', 'error')
        }
    })
})

// DELETE
function openDeleteModal(id) {
    state.pendingDeleteAction = async () => {
        const response = await apiRequest(`/api/users/${id}`, 'DELETE')
        if (!response.ok) {
            throw new Error('Failed to delete user')
        }

        showToast('User deleted')
        await loadUsers()
    }

    document.getElementById('delete-modal-title').textContent = 'Delete user'
    document.getElementById('delete-modal-text').textContent = 'Are you sure you want to delete this user?'
    document.getElementById('confirm-delete-btn').textContent = 'Delete'
    openModal('modal-delete')
}

function openDeleteAllMoviesModal() {
    state.pendingDeleteAction = async () => {
        const result = await deleteAllMovies()
        state.feedInitialized = true
        renderMovies([])
        showToast(result.message || 'Movies successfully deleted')
    }

    document.getElementById('delete-modal-title').textContent = 'Delete all movies'
    document.getElementById('delete-modal-text').textContent = 'Are you sure you want to delete all movies? This action cannot be undone.'
    document.getElementById('confirm-delete-btn').textContent = 'Delete all'
    openModal('modal-delete')
}

document.getElementById('confirm-delete-btn').addEventListener('click', async () => {
    if (!state.pendingDeleteAction) {
        closeModal('modal-delete')
        return
    }

    try {
        await state.pendingDeleteAction()
        closeModal('modal-delete')
    } catch (error) {
        showToast('Error while deleting', 'error')
    } finally {
        state.pendingDeleteAction = null
    }
})

document.getElementById('cancel-delete-btn').addEventListener('click', () => {
    state.pendingDeleteAction = null
    closeModal('modal-delete')
})

// EDIT
let pendingEditId = null

function openEditModal(id, username) {
    pendingEditId = id
    document.getElementById('edit-username').value = username
    openModal('modal-edit')
}

document.getElementById('confirm-edit-btn').addEventListener('click', async () => {
    const username = document.getElementById('edit-username').value.trim()
    if (!username) return

    const response = await apiRequest(`/api/users/${pendingEditId}`, 'PUT', { username })
    if (response.ok) {
        closeModal('modal-edit')
        showToast('User updated')
        loadUsers()
    } else {
        showToast('Error updating user', 'error')
    }
})

document.getElementById('cancel-edit-btn').addEventListener('click', () => {
    closeModal('modal-edit')
})

// CREATE
document.getElementById('create-user-btn').addEventListener('click', () => {
    document.getElementById('create-username').value = ''
    document.getElementById('create-password').value = ''
    openModal('modal-create')
})

document.getElementById('confirm-create-btn').addEventListener('click', async () => {
    const username = document.getElementById('create-username').value.trim()
    const password = document.getElementById('create-password').value.trim()
    if (!username || !password) return

    const response = await apiRequest('/api/users', 'POST', { username, password })
    if (response.ok) {
        closeModal('modal-create')
        showToast('User created')
        loadUsers()
    } else {
        showToast('Error creating user', 'error')
    }
})

document.getElementById('cancel-create-btn').addEventListener('click', () => {
    closeModal('modal-create')
})

// MOVIE ACTIONS
document.getElementById('feed-btn').addEventListener('click', async () => {
    try {
        await loadMovies({ forceFeed: true })
    } catch (error) {
        showToast('Error updating feed', 'error')
    }
})

document.getElementById('movies-refresh-btn').addEventListener('click', async () => {
    try {
        await loadMovies()
    } catch (error) {
        showToast('Error loading movies', 'error')
    }
})

document.getElementById('movies-delete-all-btn').addEventListener('click', () => {
    openDeleteAllMoviesModal()
})

document.getElementById('close-movie-modal-btn').addEventListener('click', () => {
    closeModal('modal-movie-details')
})

document.getElementById('modal-delete').addEventListener('click', event => {
    if (event.target.id === 'modal-delete') {
        state.pendingDeleteAction = null
        closeModal('modal-delete')
    }
})

document.getElementById('modal-movie-details').addEventListener('click', event => {
    if (event.target.id === 'modal-movie-details') {
        closeModal('modal-movie-details')
    }
})

// LOGOUT
document.getElementById('logout-btn').addEventListener('click', () => {
    localStorage.removeItem('token')
    window.location.href = '/login'
})

// REFRESH
document.getElementById('refresh-btn').addEventListener('click', () => {
    loadUsers()
})

// INIT
if (ensureAdminPageAccess()) {
    loadUsers().catch(() => {
        showToast('Error loading users', 'error')
    })
}
