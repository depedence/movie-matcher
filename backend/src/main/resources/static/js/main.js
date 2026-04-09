const token = localStorage.getItem('token')

// ─── API ───────────────────────────────────────────────
async function apiRequest(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }
    if (body) options.body = JSON.stringify(body)
    return await fetch(url, options)
}

// ─── TOAST ─────────────────────────────────────────────
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast')
    toast.textContent = message
    toast.className = `toast ${type}`
    setTimeout(() => toast.classList.add('hidden'), 3000)
}

// ─── MODAL ─────────────────────────────────────────────
function openModal(id) {
    document.getElementById(id).classList.remove('hidden')
}

function closeModal(id) {
    document.getElementById(id).classList.add('hidden')
}

// ─── USERS ─────────────────────────────────────────────
async function loadUsers() {
    const response = await apiRequest('/api/users')
    const users = await response.json()

    const tbody = document.querySelector('#users-table tbody')
    tbody.innerHTML = ''

    users.forEach(user => {
        const row = document.createElement('tr')
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>
                <button class="btn-edit" onclick="openEditModal(${user.id}, '${user.username}')">Edit</button>
                <button class="btn-delete" onclick="openDeleteModal(${user.id})">Delete</button>
            </td>
        `
        tbody.appendChild(row)
    })
}

// ─── DELETE ────────────────────────────────────────────
let pendingDeleteId = null

function openDeleteModal(id) {
    pendingDeleteId = id
    openModal('modal-delete')
}

document.getElementById('confirm-delete-btn').addEventListener('click', async () => {
    await apiRequest(`/api/users/${pendingDeleteId}`, 'DELETE')
    closeModal('modal-delete')
    showToast('User deleted')
    loadUsers()
})

document.getElementById('cancel-delete-btn').addEventListener('click', () => {
    closeModal('modal-delete')
})

// ─── EDIT ──────────────────────────────────────────────
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

// ─── CREATE ────────────────────────────────────────────
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

// ─── LOGOUT ────────────────────────────────────────────
document.getElementById('logout-btn').addEventListener('click', () => {
    localStorage.removeItem('token')
    window.location.href = '/login'
})

// ─── REFRESH ───────────────────────────────────────────
document.getElementById('refresh-btn').addEventListener('click', () => {
    loadUsers()
})

// ─── INIT ──────────────────────────────────────────────
loadUsers()