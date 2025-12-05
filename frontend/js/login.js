// Login/Registration JavaScript

const API_BASE = 'http://localhost:8080/api';

// Show/hide forms
function showSignup() {
  document.getElementById("loginForm").classList.add("hidden");
  document.getElementById("signupForm").classList.remove("hidden");
}

function showLogin() {
  document.getElementById("signupForm").classList.add("hidden");
  document.getElementById("loginForm").classList.remove("hidden");
}

// Handle Login
async function handleLogin(event) {
  event.preventDefault();
  
  const username = document.getElementById("loginEmail").value.trim();
  const password = document.getElementById("loginPassword").value;
  
  if (!username || !password) {
    alert("Please enter both username and password");
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      // Store user info in localStorage
      localStorage.setItem('loggedIn', 'true');
      localStorage.setItem('userId', data.id);
      localStorage.setItem('username', data.username);
      if (data.displayName) {
        localStorage.setItem('displayName', data.displayName);
      }
      
      // Redirect to profile or home page
      alert('Login successful!');
      window.location.href = 'profile.html';
    } else {
      alert(data.error || 'Login failed');
    }
  } catch (error) {
    console.error('Login error:', error);
    alert('An error occurred during login. Please try again.');
  }
}

// Handle Signup
async function handleSignup(event) {
  event.preventDefault();
  
  const username = document.getElementById("signupEmail").value.trim();
  const password = document.getElementById("signupPassword").value;
  const confirmPassword = document.getElementById("signupConfirm").value;
  
  // Validation
  if (!username || !password || !confirmPassword) {
    alert("Please fill in all fields");
    return;
  }
  
  if (password !== confirmPassword) {
    alert("Passwords do not match");
    return;
  }
  
  if (password.length < 6) {
    alert("Password must be at least 6 characters long");
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      // Store user info in localStorage
      localStorage.setItem('loggedIn', 'true');
      localStorage.setItem('userId', data.id);
      localStorage.setItem('username', data.username);
      
      // Show success and redirect
      alert('Registration successful! Welcome to Wildlife Tracker.');
      window.location.href = 'profile.html';
    } else {
      alert(data.error || 'Registration failed');
    }
  } catch (error) {
    console.error('Registration error:', error);
    alert('An error occurred during registration. Please try again.');
  }
}

// Initialize when page loads
document.addEventListener('DOMContentLoaded', () => {
  // Attach event listeners to login form
  const loginForm = document.querySelector('#loginForm button');
  if (loginForm) {
    loginForm.addEventListener('click', handleLogin);
  }
  
  // Attach event listeners to signup form
  const signupForm = document.querySelector('#signupForm button');
  if (signupForm) {
    signupForm.addEventListener('click', handleSignup);
  }
  
  // Allow Enter key to submit
  document.getElementById('loginPassword')?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleLogin(e);
  });
  
  document.getElementById('signupConfirm')?.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleSignup(e);
  });
});
