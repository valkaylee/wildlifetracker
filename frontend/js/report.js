// Report Sighting JavaScript

const API_BASE = 'http://localhost:8080/api';

// Get current user ID from localStorage (you may need to adjust this based on your auth system)
function getCurrentUserId() {
  // For now, we'll use a default or get from localStorage
  // In a real app, this would come from your authentication system
  return localStorage.getItem('userId') || '1'; // Default to user 1 for demo
}

// Handle file input change
document.addEventListener('DOMContentLoaded', () => {
  const fileInput = document.getElementById('photo');
  const fileName = document.getElementById('fileName');
  
  if (fileInput) {
    fileInput.addEventListener('change', (e) => {
      const file = e.target.files[0];
      if (file) {
        fileName.textContent = file.name;
      } else {
        fileName.textContent = 'No file chosen';
      }
    });
  }

  // Handle form submission
  const form = document.getElementById('sightingForm');
  if (form) {
    form.addEventListener('submit', handleSubmit);
  }
});

// Handle form submission
async function handleSubmit(e) {
  e.preventDefault();
  
  const submitBtn = document.querySelector('.submit-btn');
  const originalText = submitBtn.innerHTML;
  
  // Disable button and show loading state
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<span class="material-symbols-rounded">hourglass_empty</span><span>Submitting...</span>';
  
  try {
    const formData = new FormData(e.target);
    const species = formData.get('species');
    const location = formData.get('location');
    const description = formData.get('description');
    const photoFile = formData.get('photo');
    
    // For now, we'll handle image upload as a URL string
    // In a real app, you'd upload the file to a server and get a URL back
    let imageUrl = null;
    
    if (photoFile && photoFile.size > 0) {
      // Convert image to base64 for demo (in production, upload to server)
      imageUrl = await convertToBase64(photoFile);
    }
    
    // Get user ID (you'll need to implement proper user authentication)
    const userId = getCurrentUserId();
    
    // Create sighting object
    const sighting = {
      species: species,
      location: location,
      description: description,
      imageUrl: imageUrl,
      // Note: In a real app, the backend should handle user association
      // For now, we'll send userId and let backend handle it
    };
    
    // Send to backend
    const response = await fetch(`${API_BASE}/sightings`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sighting)
    });
    
    if (!response.ok) {
      throw new Error('Failed to submit sighting');
    }
    
    const result = await response.json();
    
    // Show success message
    showMessage('Sighting submitted successfully!', 'success');
    
    // Reset form
    e.target.reset();
    document.getElementById('fileName').textContent = 'No file chosen';
    
    // Optionally redirect to profile or map
    setTimeout(() => {
      window.location.href = 'profile.html';
    }, 1500);
    
  } catch (error) {
    console.error('Error submitting sighting:', error);
    showMessage('Failed to submit sighting. Please try again.', 'error');
  } finally {
    // Re-enable button
    submitBtn.disabled = false;
    submitBtn.innerHTML = originalText;
  }
}

// Convert file to base64 (for demo purposes)
function convertToBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

// Show message to user
function showMessage(message, type) {
  // Remove existing message if any
  const existingMessage = document.querySelector('.form-message');
  if (existingMessage) {
    existingMessage.remove();
  }
  
  // Create message element
  const messageEl = document.createElement('div');
  messageEl.className = `form-message form-message-${type}`;
  messageEl.textContent = message;
  
  // Insert after form header
  const formBody = document.querySelector('.form-body');
  formBody.insertBefore(messageEl, formBody.firstChild);
  
  // Remove message after 5 seconds
  setTimeout(() => {
    messageEl.remove();
  }, 5000);
}

