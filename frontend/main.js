let feed = []
let currentIndex = 0

function showLoading() {
    document.getElementById("loading").style.display = "block"
    document.getElementById("movie-card").style.display = "none"
    document.getElementById("controls").style.display = "none"
}

function hideLoading() {
    document.getElementById("loading").style.display = "none"
    document.getElementById("movie-card").style.display = "block"
    document.getElementById("controls").style.display = "block"
}

async function loadFeed(userId) {
    showLoading()
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/api/movies/feed',
            params: { userId: userId },
            headers: {
                'Content-Type': 'application/json'
            }
        })
        feed = response.data
        currentIndex = 0
        hideLoading()
        showMovie(currentIndex)
    } catch (error) {
        console.error("Ошибка при загрузке feed: ", error)
        document.getElementById("title").textContent = "Ошибка: " + error.message
    }
}

function showMovie(index) {
    if (!feed.length) return
    const movie = feed[index]
    document.getElementById("poster").src = movie.posterUrl || "";
    document.getElementById("title").textContent = movie.title || "";
    document.getElementById("year").textContent = movie.releaseYear || "";
    document.getElementById("description").textContent = movie.description || "";
    document.getElementById("rating").textContent = movie.rating ? "Рейтинг: " + movie.rating : "";
}

document.getElementById("next-btn").addEventListener("click", () => {
    currentIndex++
    if (currentIndex >= feed.length) currentIndex = 0
    showMovie(currentIndex)
})

async function init() {
    showLoading()
    await loadFeed(1)
}

init()