document.getElementById('login-btn').addEventListener('click', async () => {
    const username = document.getElementById('login-input').value
    const password = document.getElementById('password-input').value

    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })

    if (response.ok) {
        const data = await response.json()
        localStorage.setItem('token', data.token)
        await new Promise(resolve => setTimeout(resolve, 100))
        window.location.href = '/main'
    } else {
        alert('Неверный логин или пароль')
    }
})