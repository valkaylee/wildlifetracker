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
    
    // Fetch user info
    const userResponse = await fetch(`${API_BASE}/users/${userId}`);
    if (!userResponse.ok) {
      throw new Error('Failed to load user info');
    }
    const userData = await userResponse.json();
    
    // Fetch user's sightings
    const sightingsResponse = await fetch(`${API_BASE}/sightings/user/${userId}`);
    if (!sightingsResponse.ok) {
      throw new Error('Failed to load sightings');
    }
    const sightings = await sightingsResponse.json();
    
    // Combine data for display
    const profileData = {
      displayName: userData.displayName || 'Wildlife Observer',
      bio: userData.bio || 'No bio yet. Add a description in settings!',
      profilePictureUrl: userData.profilePictureUrl,
      recentSightings: sightings,
      statistics: {
        totalSightings: userData.totalAnimalsLogged || 0,
        uniqueSpecies: userData.uniqueSpeciesCount || 0
      }
    };
    
    console.log('Sightings received:', sightings);
    sightings.forEach((s, i) => {
      console.log(`Sighting ${i}:`, s.species, 'ImageUrl:', s.imageUrl);
    });
    
    displayProfile(profileData);
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
      recentSightings: []
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
  
  // Species list - calculate from sightings
  const sightings = data.recentSightings || [];
  displaySpeciesList(sightings);
  
  // Recent sightings
  displayRecentSightings(sightings);
  
  // Photo gallery - filter sightings with images
  displayPhotoGallery(sightings.filter(s => s.imageUrl));
}

// Display species list
function displaySpeciesList(sightings) {
  const container = document.getElementById('speciesList');
  
  if (sightings.length === 0) {
    container.innerHTML = '<p class="empty-message">No sightings yet. Start exploring campus!</p>';
    return;
  }
  
  // Count species from sightings
  const speciesMap = {};
  sightings.forEach(s => {
    const species = s.species || 'Unknown Species';
    speciesMap[species] = (speciesMap[species] || 0) + 1;
  });
  
  container.innerHTML = Object.entries(speciesMap).map(([species, count]) => 
    `<div class="species-item">${species} (${count})</div>`
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
    // Ensure imageUrl is absolute URL
    const imageUrl = sighting.imageUrl ? (sighting.imageUrl.startsWith('http') ? sighting.imageUrl : `http://localhost:8080${sighting.imageUrl}`) : null;
    const imageHtml = imageUrl 
      ? `<img src="${imageUrl}" alt="${sighting.species}" class="sighting-image" />`
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
function displayPhotoGallery(sightings) {
  const container = document.getElementById('photoGallery');
  
  if (sightings.length === 0) {
    container.innerHTML = '<p class="empty-message">No photos yet. Add photos to your sightings!</p>';
    return;
  }
  
  container.innerHTML = sightings.map(sighting => {
    // Ensure imageUrl is absolute URL
    const imageUrl = sighting.imageUrl ? (sighting.imageUrl.startsWith('http') ? sighting.imageUrl : `http://localhost:8080${sighting.imageUrl}`) : null;
    return `
      <div class="photo-item">
        <img src="${imageUrl}" alt="${sighting.species}" />
        <div class="photo-overlay">${sighting.species}</div>
      </div>
    `;
  }).join('');
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
  loadProfile();
});

