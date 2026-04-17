const loginBtn = document.getElementById('login-btn')
const usernameInput = document.getElementById('login-input')
const passwordInput = document.getElementById('password-input')
const errorBanner = document.getElementById('error-banner')
const usernameError = document.getElementById('username-error')
const passwordError = document.getElementById('password-error')

function clearErrors() {
    errorBanner.classList.remove('visible')
    errorBanner.textContent = ''

    usernameInput.classList.remove('input-error')
    passwordInput.classList.remove('input-error')

    usernameError.classList.remove('visible')
    passwordError.classList.remove('visible')
    usernameError.textContent = ''
    passwordError.textContent = ''
}

function showFieldError(input, errorEl, message) {
    input.classList.add('input-error')
    errorEl.textContent = message
    errorEl.classList.add('visible')
}

function validate(username, password) {
    let isValid = true

    if (!username.trim()) {
        showFieldError(usernameInput, usernameError, 'Username is required')
        isValid = false
    } else if (username.trim().length < 3) {
        showFieldError(usernameInput, usernameError, 'Minimum 3 characters')
        isValid = false
    }

    if (!password.trim()) {
        showFieldError(passwordInput, passwordError, 'Password is required')
        isValid = false
    } else if (password.trim().length < 6) {
        showFieldError(passwordInput, passwordError, 'Minimum 6 characters')
        isValid = false
    }

    return isValid
}

loginBtn.addEventListener('click', async () => {
    clearErrors()

    const username = usernameInput.value
    const password = passwordInput.value

    if (!validate(username, password)) return

    loginBtn.disabled = true
    loginBtn.textContent = 'Logging in...'

    try {
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
            const errorData = await response.json()
            errorBanner.textContent = errorData.message || 'Something went wrong'
            errorBanner.classList.add('visible')
        }
    } catch (e) {
        errorBanner.textContent = 'Network error. Please try again.'
        errorBanner.classList.add('visible')
    } finally {
        loginBtn.disabled = false
        loginBtn.textContent = 'Login'
    }
})

usernameInput.addEventListener('input', () => {
    usernameInput.classList.remove('input-error')
    usernameError.classList.remove('visible')
    errorBanner.classList.remove('visible')
})

passwordInput.addEventListener('input', () => {
    passwordInput.classList.remove('input-error')
    passwordError.classList.remove('visible')
    errorBanner.classList.remove('visible')
})