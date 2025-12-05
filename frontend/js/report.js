// Report Sighting JavaScript

const API_BASE = 'http://localhost:8080/api';

// Get current user ID from localStorage (you may need to adjust this based on your auth system)
function getCurrentUserId() {
  // For now, we'll use a default or get from localStorage
  // In a real app, this would come from your authentication system
  return localStorage.getItem('userId') || '1'; // Default to user 1 for demo
}

// Reverse location lookup - find nearest location name to coordinates
function getLocationNameFromCoordinates(pixelX, pixelY) {
  const LOCATION_COORDINATES = {
    'trousdale parkway': { x: 45, y: 50 },
    'alumni park': { x: 50, y: 45 },
    'doheny library': { x: 48, y: 48 },
    'mccarthy quad': { x: 46, y: 46 },
    'exposition park': { x: 55, y: 60 },
    'hahn plaza': { x: 47, y: 47 },
    'founders park': { x: 49, y: 49 },
    'bovard building': { x: 47, y: 46 },
    'campus center': { x: 48, y: 47 },
    'leavey library': { x: 50, y: 48 },
    'galen center': { x: 52, y: 55 },
    'school of engineering': { x: 44, y: 48 },
    'science center': { x: 45, y: 47 },
    'school of cinematic arts': { x: 43, y: 45 },
    'school of music': { x: 44, y: 44 },
    'shrine auditorium': { x: 51, y: 44 },
    'university club': { x: 49, y: 46 },
    'athletic center': { x: 53, y: 52 },
    'loker track stadium': { x: 54, y: 53 },
    'dedeaux stadium': { x: 55, y: 54 },
  };
  
  // Find the closest location
  let closestLocation = 'Campus';
  let closestDistance = Infinity;
  
  Object.entries(LOCATION_COORDINATES).forEach(([name, coords]) => {
    const distance = Math.sqrt(Math.pow(pixelX - coords.x, 2) + Math.pow(pixelY - coords.y, 2));
    if (distance < closestDistance) {
      closestDistance = distance;
      closestLocation = name;
    }
  });
  
  return closestDistance < 10 ? closestLocation : 'Campus (Custom Location)';
}

// Handle file input change
document.addEventListener('DOMContentLoaded', () => {
  console.log('DOM Content Loaded');
  
  const fileInput = document.getElementById('photo');
  const fileName = document.getElementById('fileName');
  
  console.log('File input found:', !!fileInput);
  console.log('File name element found:', !!fileName);
  
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

  // Handle map picker clicks
  const mapPicker = document.getElementById('mapPicker');
  if (mapPicker) {
    mapPicker.addEventListener('click', (e) => {
      handleMapClick(e);
    });
  }

  // Handle form submission
  const form = document.getElementById('sightingForm');
  console.log('Form found:', !!form);
  
  if (form) {
    form.addEventListener('submit', (e) => {
      console.log('Submit event fired');
      handleSubmit(e);
    });
  } else {
    console.error('Form with id "sightingForm" not found');
  }
});

// Handle map clicks to select location
function handleMapClick(e) {
  const mapPicker = document.getElementById('mapPicker');
  const mapRect = mapPicker.getBoundingClientRect();
  
  // Calculate click position relative to map
  const clickX = e.clientX - mapRect.left;
  const clickY = e.clientY - mapRect.top;
  
  // Convert to percentage coordinates
  const percentX = (clickX / mapRect.width) * 100;
  const percentY = (clickY / mapRect.height) * 100;
  
  // Store the coordinates
  document.getElementById('pixelX').value = percentX;
  document.getElementById('pixelY').value = percentY;
  
  // Get location name
  const locationName = getLocationNameFromCoordinates(percentX, percentY);
  document.getElementById('location').value = locationName;
  
  // Display marker
  const marker = document.getElementById('mapPickerMarker');
  marker.style.left = percentX + '%';
  marker.style.top = percentY + '%';
  marker.style.display = 'block';
  
  // Update location display
  const locationDisplay = document.getElementById('locationDisplay');
  locationDisplay.innerHTML = `
    <p><strong>Selected Location:</strong> ${locationName}</p>
    <p class="coordinates-hint">Coordinates: ${percentX.toFixed(1)}%, ${percentY.toFixed(1)}%</p>
  `;
  
  console.log('Location selected:', { locationName, percentX, percentY });
}

// Handle form submission
async function handleSubmit(e) {
  e.preventDefault();
  console.log('Form submitted');
  
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
    const pixelX = parseFloat(formData.get('pixelX'));
    const pixelY = parseFloat(formData.get('pixelY'));
    
    console.log('Form data:', { species, location, description, hasFile: !!photoFile, pixelX, pixelY });
    
    // Validate location was selected
    if (!location || !pixelX || !pixelY) {
      throw new Error('Please select a location on the map');
    }
    
    // Get user ID from localStorage (required!)
    const userId = parseInt(localStorage.getItem('userId'));
    
    console.log('User ID:', userId);
    
    if (!userId || isNaN(userId)) {
      throw new Error('User not logged in. Please log in first. UserId: ' + userId);
    }
    
    let imageUrl = null;
    
    // Upload image if provided
    if (photoFile && photoFile.size > 0) {
      console.log('Uploading image...');
      const imageFormData = new FormData();
      imageFormData.append('file', photoFile);
      
      const uploadResponse = await fetch(`${API_BASE}/sightings/upload-image`, {
        method: 'POST',
        body: imageFormData
      });
      
      console.log('Upload response status:', uploadResponse.status);
      
      if (!uploadResponse.ok) {
        const error = await uploadResponse.text();
        throw new Error('Failed to upload image: ' + error);
      }
      
      const uploadResult = await uploadResponse.json();
      imageUrl = uploadResult.imageUrl;
      console.log('Image uploaded:', imageUrl);
    }
    
    // Create sighting object with pixel coordinates
    const sighting = {
      species: species,
      location: location,
      description: description,
      userId: userId,
      imageUrl: imageUrl,
      pixelX: pixelX,
      pixelY: pixelY
    };
    
    console.log('Sighting object:', sighting);
    
    // Send to backend
    const response = await fetch(`${API_BASE}/sightings`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sighting)
    });
    
    console.log('Sighting response status:', response.status);
    
    if (!response.ok) {
      const error = await response.text();
      throw new Error('Failed to submit sighting: ' + error);
    }
    
    const result = await response.json();
    console.log('Sighting result:', result);
    
    // Show success message
    showMessage('Sighting submitted successfully!', 'success');
    
    // Reset form
    e.target.reset();
    document.getElementById('fileName').textContent = 'No file chosen';
    document.getElementById('mapPickerMarker').style.display = 'none';
    document.getElementById('locationDisplay').innerHTML = '<p>No location selected yet</p>';
    
    // Optionally redirect to profile or map
    setTimeout(() => {
      window.location.href = 'profile.html';
    }, 1500);
    
  } catch (error) {
    console.error('Error submitting sighting:', error);
    showMessage('Failed to submit sighting: ' + error.message, 'error');
    // Re-enable button
    submitBtn.disabled = false;
    submitBtn.innerHTML = originalText;
  }
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

