const token = localStorage.getItem('token')

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

async function apiRequest(url, method = 'GET') {
    return fetch(url, {
        method,
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
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

async function fetchMovies() {
    const response = await apiRequest('/api/movies')
    if (!response.ok) {
        throw new Error('Failed to load movies')
    }
    return await response.json()
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

function renderFeaturedMovie(movie) {
    const featuredCard = document.getElementById('featured-movie')
    featuredCard.classList.remove('hidden')

    document.getElementById('featured-title').textContent = formatValue(movie.title)
    document.getElementById('featured-description').textContent = formatValue(movie.description, 'No description')
    document.getElementById('featured-year').textContent = formatValue(movie.releaseYear)
    document.getElementById('featured-rating').textContent = formatValue(movie.rating)
    document.getElementById('featured-imdb').textContent = formatValue(movie.imdbId)

    renderTags(document.getElementById('featured-genres'), movie.genres)
    setPoster(
        document.getElementById('featured-poster'),
        document.getElementById('featured-poster-fallback'),
        movie.posterUrl,
        `${formatValue(movie.title)} poster`
    )
}

function openMovieModal(movie) {
    document.getElementById('movie-modal-title').textContent = formatValue(movie.title)
    document.getElementById('movie-modal-id').textContent = formatValue(movie.id)
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

function renderMovies(movies) {
    const grid = document.getElementById('movies-grid')
    grid.innerHTML = ''

    movies.forEach(movie => {
        const card = document.createElement('article')
        card.className = 'movie-card'
        card.innerHTML = `
            <div class="movie-card-poster-wrap">
                <img class="movie-card-poster hidden" alt="${formatValue(movie.title)} poster">
                <div class="movie-card-poster-fallback">No poster</div>
            </div>
            <div class="movie-card-body">
                <div class="movie-card-head">
                    <h3>${formatValue(movie.title)}</h3>
                    <span class="movie-rating">${formatValue(movie.rating)}</span>
                </div>
                <p class="movie-year">${formatValue(movie.releaseYear)}</p>
                <p class="movie-description-preview">${formatValue(movie.description, 'No description')}</p>
                <div class="tag-list card-tags"></div>
            </div>
        `

        const poster = card.querySelector('.movie-card-poster')
        const fallback = card.querySelector('.movie-card-poster-fallback')
        setPoster(poster, fallback, movie.posterUrl, `${formatValue(movie.title)} poster`)
        renderTags(card.querySelector('.card-tags'), movie.genres)

        card.addEventListener('click', () => openMovieModal(movie))
        grid.appendChild(card)
    })
}

function setViewState({ loading = false, empty = false, error = false, hasGrid = false }) {
    document.getElementById('home-loading').classList.toggle('hidden', !loading)
    document.getElementById('home-empty').classList.toggle('hidden', !empty)
    document.getElementById('home-error').classList.toggle('hidden', !error)
    document.getElementById('movies-grid').classList.toggle('hidden', !hasGrid)
    document.getElementById('featured-movie').classList.toggle('hidden', !hasGrid)
}

async function loadHomePage() {
    setViewState({ loading: true })

    try {
        const movies = await fetchMovies()
        document.getElementById('movies-count').textContent = `${movies.length} movie${movies.length === 1 ? '' : 's'}`

        if (movies.length === 0) {
            setViewState({ empty: true })
            return
        }

        renderFeaturedMovie(movies[0])
        renderMovies(movies)
        setViewState({ hasGrid: true })
    } catch (error) {
        setViewState({ error: true })
    }
}

document.getElementById('home-refresh-btn').addEventListener('click', async () => {
    await loadHomePage()
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

if (ensureAuthorizedUserPage()) {
    loadHomePage()
}
