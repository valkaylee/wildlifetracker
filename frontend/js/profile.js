// Profile page JavaScript

const API_BASE = 'http://localhost:8080/api';

// Get current user ID from localStorage (you may need to adjust this based on your auth system)
function getCurrentUserId() {
  // For now, we'll use a default or get from localStorage
  // In a real app, this would come from your authentication system
  return localStorage.getItem('userId') || '1'; // Default to user 1 for demo
}

// Fetch user profile data
async function loadProfile() {
  try {
    const userId = getCurrentUserId();
    const response = await fetch(`${API_BASE}/profile/${userId}`);
    
    if (!response.ok) {
      throw new Error('Failed to load profile');
    }
    
    const data = await response.json();
    displayProfile(data);
  } catch (error) {
    console.error('Error loading profile:', error);
    // Show default/empty state
    displayProfile({
      displayName: 'Wildlife Observer',
      bio: 'No bio yet. Add a description in settings!',
      profilePictureUrl: null,
      statistics: {
        totalSightings: 0,
        uniqueSpecies: 0,
        favoriteSpecies: null,
        favoriteSpeciesCount: 0
      },
      speciesList: [],
      recentSightings: [],
      photos: []
    });
  }
}

// Display profile data
function displayProfile(data) {
  // Profile picture and basic info
  const profilePicture = document.getElementById('profilePicture');
  const displayName = document.getElementById('displayName');
  const userBio = document.getElementById('userBio');
  
  if (data.profilePictureUrl) {
    profilePicture.src = data.profilePictureUrl;
  }
  
  if (data.displayName) {
    displayName.textContent = data.displayName;
  }
  
  if (data.bio) {
    userBio.textContent = data.bio;
  }
  
  // Statistics
  const stats = data.statistics || {};
  document.getElementById('totalSightings').textContent = stats.totalSightings || 0;
  document.getElementById('uniqueSpecies').textContent = stats.uniqueSpecies || 0;
  
  const favoriteSpeciesEl = document.getElementById('favoriteSpecies');
  const favoriteSpeciesCountEl = document.getElementById('favoriteSpeciesCount');
  
  if (stats.favoriteSpecies) {
    favoriteSpeciesEl.textContent = stats.favoriteSpecies;
    favoriteSpeciesCountEl.textContent = `${stats.favoriteSpeciesCount || 0} sightings`;
  } else {
    favoriteSpeciesEl.textContent = 'N/A';
    favoriteSpeciesCountEl.textContent = '0 sightings';
  }
  
  // Species list
  displaySpeciesList(data.speciesList || []);
  
  // Recent sightings
  displayRecentSightings(data.recentSightings || []);
  
  // Photo gallery
  displayPhotoGallery(data.photos || []);
}

// Display species list
function displaySpeciesList(speciesList) {
  const container = document.getElementById('speciesList');
  
  if (speciesList.length === 0) {
    container.innerHTML = '<p class="empty-message">No sightings yet. Start exploring campus!</p>';
    return;
  }
  
  container.innerHTML = speciesList.map(species => 
    `<div class="species-item">${species.name} (${species.count})</div>`
  ).join('');
}

// Display recent sightings
function displayRecentSightings(sightings) {
  const container = document.getElementById('recentSightings');
  
  if (sightings.length === 0) {
    container.innerHTML = '<p class="empty-message">No sightings yet. Report your first wildlife encounter!</p>';
    return;
  }
  
  container.innerHTML = sightings.map(sighting => {
    const date = new Date(sighting.timestamp).toLocaleDateString();
    const imageHtml = sighting.imageUrl 
      ? `<img src="${sighting.imageUrl}" alt="${sighting.species}" class="sighting-image" />`
      : `<div class="sighting-image" style="background: #e0e0e0; display: flex; align-items: center; justify-content: center; color: #999;">No Image</div>`;
    
    return `
      <div class="sighting-card">
        ${imageHtml}
        <div class="sighting-info">
          <h3 class="sighting-species">${sighting.species || 'Unknown Species'}</h3>
          <p class="sighting-location">${sighting.location || 'Unknown Location'}</p>
          <p class="sighting-date">${date}</p>
        </div>
      </div>
    `;
  }).join('');
}

// Display photo gallery
function displayPhotoGallery(photos) {
  const container = document.getElementById('photoGallery');
  
  if (photos.length === 0) {
    container.innerHTML = '<p class="empty-message">No photos yet. Add photos to your sightings!</p>';
    return;
  }
  
  container.innerHTML = photos.map(photo => {
    return `
      <div class="photo-item">
        <img src="${photo.imageUrl}" alt="${photo.species}" />
        <div class="photo-overlay">${photo.species}</div>
      </div>
    `;
  }).join('');
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
  loadProfile();
});

