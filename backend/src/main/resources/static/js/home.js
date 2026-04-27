const token = localStorage.getItem('token')

const state = {
    currentMovie: null,
    recentLikes: [],
    isAnimating: false,
    toastTimerId: null
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

function formatValue(value, fallback = '—') {
    if (value === null || value === undefined || value === '') {
        return fallback
    }
    return value
}

function formatGenres(genres) {
    if (!Array.isArray(genres) || genres.length === 0) {
        return []
    }
    return genres
}

async function apiRequest(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }

    if (body) {
        options.headers['Content-Type'] = 'application/json'
        options.body = JSON.stringify(body)
    }

    return fetch(url, options)
}

function ensureAuthorizedUserPage() {
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

    if (payload.role === 'ADMIN') {
        window.location.href = '/admin'
        return false
    }

    return true
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast')
    clearTimeout(state.toastTimerId)

    toast.textContent = message
    toast.className = `toast ${type}`
    toast.classList.remove('hidden')

    state.toastTimerId = window.setTimeout(() => {
        toast.classList.add('hidden')
    }, 2600)
}

function setPoster(imageEl, fallbackEl, posterUrl, altText) {
    if (posterUrl && posterUrl !== 'N/A') {
        imageEl.src = posterUrl
        imageEl.alt = altText
        imageEl.classList.remove('hidden')
        fallbackEl.classList.add('hidden')
        return
    }

    imageEl.removeAttribute('src')
    imageEl.classList.add('hidden')
    fallbackEl.classList.remove('hidden')
}

function renderTags(container, genres, emptyLabel = 'No genres') {
    container.innerHTML = ''

    const values = formatGenres(genres)
    if (values.length === 0) {
        const tag = document.createElement('span')
        tag.className = 'tag'
        tag.textContent = emptyLabel
        container.appendChild(tag)
        return
    }

    values.forEach(genre => {
        const tag = document.createElement('span')
        tag.className = 'tag'
        tag.textContent = genre
        container.appendChild(tag)
    })
}

function openMovieModal(movie) {
    if (!movie) {
        return
    }

    document.getElementById('movie-modal-title').textContent = formatValue(movie.title)
    document.getElementById('movie-modal-imdb').textContent = formatValue(movie.imdbId)
    document.getElementById('movie-modal-year').textContent = formatValue(movie.releaseYear)
    document.getElementById('movie-modal-rating').textContent = formatValue(movie.rating)
    document.getElementById('movie-modal-description').textContent = formatValue(movie.description, 'No description')

    renderTags(document.getElementById('movie-modal-genres'), movie.genres)
    setPoster(
        document.getElementById('movie-modal-poster'),
        document.getElementById('movie-modal-poster-fallback'),
        movie.posterUrl,
        `${formatValue(movie.title)} poster`
    )

    document.getElementById('movie-modal').classList.remove('hidden')
}

function setFeedStatus(label) {
    document.getElementById('feed-status-pill').textContent = label
}

function setInteractionEnabled(enabled) {
    document.getElementById('skip-movie-btn').disabled = !enabled
    document.getElementById('like-movie-btn').disabled = !enabled
    document.getElementById('details-movie-btn').disabled = !enabled
}

function setViewState({ loading = false, empty = false, error = false, hasMovie = false }) {
    document.getElementById('home-loading').classList.toggle('hidden', !loading)
    document.getElementById('home-empty').classList.toggle('hidden', !empty)
    document.getElementById('home-error').classList.toggle('hidden', !error)
    document.getElementById('swipe-stage').classList.toggle('hidden', !hasMovie)
}

function renderCurrentMovie(movie) {
    state.currentMovie = movie

    if (!movie) {
        setViewState({ empty: true })
        setFeedStatus('No movies')
        return
    }

    setViewState({ hasMovie: true })
    setFeedStatus('Fresh pick')

    const card = document.getElementById('current-movie-card')
    card.classList.remove('swipe-left', 'swipe-right')
    void card.offsetWidth

    document.getElementById('current-movie-title').textContent = formatValue(movie.title)
    document.getElementById('current-movie-rating').textContent = formatValue(movie.rating)
    document.getElementById('current-movie-year').textContent = formatValue(movie.releaseYear)
    document.getElementById('current-movie-imdb').textContent = formatValue(movie.imdbId)
    document.getElementById('current-movie-description').textContent = formatValue(movie.description, 'No description')

    renderTags(document.getElementById('current-movie-genres'), movie.genres)
    setPoster(
        document.getElementById('current-movie-poster'),
        document.getElementById('current-movie-poster-fallback'),
        movie.posterUrl,
        `${formatValue(movie.title)} poster`
    )
}

function createLikedMovieCard(movie) {
    const card = document.createElement('article')
    card.className = 'liked-movie-card'
    card.innerHTML = `
        <div class="liked-movie-media">
            <img class="liked-movie-poster hidden" alt="${formatValue(movie.title)} poster">
            <div class="liked-movie-fallback hidden">No poster</div>
        </div>
        <div class="liked-movie-body">
            <div class="liked-movie-head">
                <h3>${formatValue(movie.title)}</h3>
                <span class="movie-rating">${formatValue(movie.rating)}</span>
            </div>
            <p class="liked-movie-year">${formatValue(movie.releaseYear)}</p>
            <p class="liked-movie-description">${formatValue(movie.description, 'No description')}</p>
            <div class="tag-list liked-tags"></div>
        </div>
    `

    setPoster(
        card.querySelector('.liked-movie-poster'),
        card.querySelector('.liked-movie-fallback'),
        movie.posterUrl,
        `${formatValue(movie.title)} poster`
    )
    renderTags(card.querySelector('.liked-tags'), movie.genres)
    card.addEventListener('click', () => openMovieModal(movie))
    return card
}

function renderRecentLikes(movies) {
    state.recentLikes = Array.isArray(movies) ? movies : []

    const grid = document.getElementById('liked-movies-grid')
    const empty = document.getElementById('likes-empty')
    const likesCount = document.getElementById('likes-count')

    likesCount.textContent = `${state.recentLikes.length} / 10`
    grid.innerHTML = ''

    if (state.recentLikes.length === 0) {
        empty.classList.remove('hidden')
        grid.classList.add('hidden')
        return
    }

    empty.classList.add('hidden')
    grid.classList.remove('hidden')
    state.recentLikes.forEach(movie => {
        grid.appendChild(createLikedMovieCard(movie))
    })
}

async function fetchFeed() {
    const response = await apiRequest('/api/feed/me')
    if (!response.ok) {
        throw new Error('Failed to load feed')
    }
    return response.json()
}

async function sendSwipe(imdbId, swipeType) {
    const response = await apiRequest('/api/feed/me/swipe', 'POST', { imdbId, swipeType })
    if (!response.ok) {
        throw new Error('Failed to submit swipe')
    }
    return response.json()
}

async function loadHomePage() {
    document.getElementById('home-refresh-btn').disabled = true
    setInteractionEnabled(false)
    setViewState({ loading: true })
    setFeedStatus('Loading')

    try {
        const payload = await fetchFeed()
        renderCurrentMovie(payload.currentMovie)
        renderRecentLikes(payload.recentLikes)
        if (payload.currentMovie) {
            setInteractionEnabled(true)
        }
        return true
    } catch (error) {
        setViewState({ error: true })
        setFeedStatus('Unavailable')
        showToast('Failed to load feed', 'error')
        return false
    } finally {
        document.getElementById('home-refresh-btn').disabled = false
    }
}

async function handleSwipe(direction) {
    if (state.isAnimating || !state.currentMovie) {
        return
    }

    state.isAnimating = true
    setInteractionEnabled(false)
    setFeedStatus(direction === 'LIKE' ? 'Saving like' : 'Skipping')

    const movie = state.currentMovie
    const card = document.getElementById('current-movie-card')
    card.classList.add(direction === 'LIKE' ? 'swipe-right' : 'swipe-left')

    try {
        await new Promise(resolve => window.setTimeout(resolve, 320))
        const payload = await sendSwipe(movie.imdbId, direction)
        renderCurrentMovie(payload.currentMovie)
        renderRecentLikes(payload.recentLikes)
        showToast(direction === 'LIKE' ? 'Movie added to likes' : 'Movie skipped')

        if (payload.currentMovie) {
            setInteractionEnabled(true)
        }
    } catch (error) {
        card.classList.remove('swipe-left', 'swipe-right')
        renderCurrentMovie(movie)
        renderRecentLikes(state.recentLikes)
        setInteractionEnabled(true)
        setFeedStatus('Retry needed')
        showToast('Failed to update swipe', 'error')
    } finally {
        state.isAnimating = false
    }
}

document.getElementById('skip-movie-btn').addEventListener('click', async () => {
    await handleSwipe('SKIP')
})

document.getElementById('like-movie-btn').addEventListener('click', async () => {
    await handleSwipe('LIKE')
})

document.getElementById('details-movie-btn').addEventListener('click', () => {
    openMovieModal(state.currentMovie)
})

document.getElementById('home-refresh-btn').addEventListener('click', async () => {
    const loaded = await loadHomePage()
    if (loaded) {
        showToast('Feed refreshed')
    }
})

document.getElementById('home-logout-btn').addEventListener('click', () => {
    localStorage.removeItem('token')
    window.location.href = '/login'
})

document.getElementById('close-movie-modal-btn').addEventListener('click', () => {
    document.getElementById('movie-modal').classList.add('hidden')
})

document.getElementById('movie-modal').addEventListener('click', event => {
    if (event.target.id === 'movie-modal') {
        document.getElementById('movie-modal').classList.add('hidden')
    }
})

document.addEventListener('keydown', async event => {
    if (document.getElementById('movie-modal').classList.contains('hidden') === false) {
        if (event.key === 'Escape') {
            document.getElementById('movie-modal').classList.add('hidden')
        }
        return
    }

    if (!state.currentMovie || state.isAnimating) {
        return
    }

    if (event.key === 'ArrowLeft') {
        await handleSwipe('SKIP')
    }

    if (event.key === 'ArrowRight') {
        await handleSwipe('LIKE')
    }
})

if (ensureAuthorizedUserPage()) {
    loadHomePage()
}
