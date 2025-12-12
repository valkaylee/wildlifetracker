// Campus Map JavaScript

const API_BASE = 'http://localhost:8080/api';

// Location to coordinate mapping (approximate positions on the map)
const LOCATION_COORDINATES = {
  'trousdale parkway': { x: 45, y: 50 },
  'alumni park': { x: 52, y: 64 },
  'doheny library': { x: 57, y: 70 },
  'near doheny library': { x: 54, y: 67 },
  'mccarthy quad': { x: 61, y: 60 },
  'founders park': { x: 47, y: 52 },
  'campus center': { x: 40, y: 68 },
  'leavey library': { x: 63, y: 50 },
  'galen center': { x: 82, y: 62 },
  'school of engineering': { x: 27, y: 70 },
  'science center': { x: 21, y: 76 },
  'school of cinematic arts': { x: 36, y: 29 },
  'school of music': { x: 48, y: 37 },
  'shrine auditorium': { x: 74, y: 30 },
  'university club': { x: 62, y: 39 },
  'athletic center': { x: 34, y: 39 },
  'loker track stadium': { x: 30, y: 47 },
  'dedeaux stadium': { x: 17, y: 24 },
  'USC village': { x: 49, y: 11 },
  'parkside': { x: 13, y: 82 },
  'EVK': { x: 68, y: 55 },
};

let allSightings = [];
let filteredSightings = [];
let searchHistory = [];

// Escape HTML to prevent XSS
function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
  loadSightings();
  setupSearch();
  loadSearchHistory();
  
  // Set up live updates (refresh every 30 seconds)
  setInterval(loadSightings, 30000);
});

// Load sightings from API
async function loadSightings() {
  try {
    const response = await fetch(`${API_BASE}/sightings`);
    if (!response.ok) {
      throw new Error('Failed to load sightings');
    }
    
    allSightings = await response.json();
    filteredSightings = allSightings;
    
    displayMapMarkers(allSightings);
    displaySightingsGrid(allSightings);
    
  } catch (error) {
    console.error('Error loading sightings:', error);
    showError('Failed to load sightings');
  }
}

// Display markers on map
function displayMapMarkers(sightings) {
  const markersContainer = document.getElementById('sightingMarkers');
  markersContainer.innerHTML = '';
  
  const mapImg = document.getElementById('campusMap');
  
  // Wait for image to load to get its dimensions
  if (mapImg.complete) {
    placeMarkers(sightings, mapImg);
  } else {
    mapImg.onload = () => placeMarkers(sightings, mapImg);
  }
}

// Place markers on the map
function placeMarkers(sightings, mapImg) {
  const markersContainer = document.getElementById('sightingMarkers');
  const mapRect = mapImg.getBoundingClientRect();
  const mapWidth = mapRect.width;
  const mapHeight = mapRect.height;
  
  sightings.forEach(sighting => {
    if (!sighting.location && (!sighting.pixelX || !sighting.pixelY)) return;
    
    let coords = null;
    
    // Priority 1: Use pixelX and pixelY if available (from map picker)
    if (sighting.pixelX !== null && sighting.pixelX !== undefined && 
        sighting.pixelY !== null && sighting.pixelY !== undefined) {
      coords = {
        x: sighting.pixelX,
        y: sighting.pixelY
      };
    } else if (sighting.location) {
      // Priority 2: Try to find location match
      const locationKey = sighting.location.toLowerCase();
      
      for (const [key, value] of Object.entries(LOCATION_COORDINATES)) {
        if (locationKey.includes(key) || key.includes(locationKey)) {
          coords = value;
          break;
        }
      }
      
      // If no match, use random position (for demo)
      if (!coords) {
        coords = {
          x: 45 + Math.random() * 10,
          y: 45 + Math.random() * 10
        };
      }
    }
    
    // Only create marker if we have valid coordinates
    if (!coords) return;
    
    // Create marker
    const marker = document.createElement('div');
    marker.className = 'sighting-marker';
    marker.style.left = `${coords.x}%`;
    marker.style.top = `${coords.y}%`;
    marker.setAttribute('data-sighting-id', sighting.id);
    marker.onclick = () => showSightingDetails(sighting);
    
    markersContainer.appendChild(marker);
  });
}

// Display sightings grid
function displaySightingsGrid(sightings) {
  const grid = document.getElementById('sightingsGrid');
  
  if (sightings.length === 0) {
    grid.innerHTML = '<p class="loading-message">No sightings found</p>';
    return;
  }
  
  // Sort by date (newest first)
  const sorted = [...sightings].sort((a, b) => {
    const dateA = new Date(a.timestamp || 0);
    const dateB = new Date(b.timestamp || 0);
    return dateB - dateA;
  });
  
  grid.innerHTML = sorted.slice(0, 10).map(sighting => {
    const date = sighting.timestamp 
      ? new Date(sighting.timestamp).toLocaleDateString('en-US', { year: 'numeric', month: '2-digit', day: '2-digit' })
      : 'Unknown';
    const displayName = sighting.displayName || 'Unknown';
    
    return `
      <div class="sighting-card" onclick="showSightingDetailsById(${sighting.id})">
        <div class="sighting-card-header">
          <h3 class="sighting-species">${sighting.species || 'Unknown Species'}</h3>
          <span class="sighting-date">${date}</span>
        </div>
        <p class="sighting-location">${sighting.location || 'Unknown Location'}</p>
        <p class="sighting-description">${sighting.description || 'No description provided.'}</p>
        <p class="sighting-user">Spotted by: ${displayName}</p>
      </div>
    `;
  }).join('');
}

// Show sighting details modal by ID
async function showSightingDetailsById(id) {
  // Find sighting in current list
  const sighting = allSightings.find(s => s.id === id) || 
                   filteredSightings.find(s => s.id === id);
  
  if (sighting) {
    showSightingDetails(sighting);
  } else {
    // Fetch from API if not in current list
    try {
      const response = await fetch(`${API_BASE}/sightings/${id}`);
      if (response.ok) {
        const sighting = await response.json();
        showSightingDetails(sighting);
      }
    } catch (error) {
      console.error('Error fetching sighting:', error);
    }
  }
}

// Show sighting details modal
function showSightingDetails(sighting) {
  const modal = document.getElementById('sightingModal');
  const modalBody = document.getElementById('modalBody');
  
  const date = sighting.timestamp 
    ? new Date(sighting.timestamp).toLocaleDateString('en-US', { year: 'numeric', month: '2-digit', day: '2-digit' })
    : 'Unknown';
  const time = sighting.timestamp
    ? new Date(sighting.timestamp).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false })
    : 'Unknown';
  const displayName = sighting.displayName || 'Unknown';
  
  // Build image HTML if imageUrl exists
  // Match the profile page logic: convert relative paths to absolute URLs
  let imageHtml = '';
  if (sighting.imageUrl) {
    let imageSrc = sighting.imageUrl;
    // If it's a relative path, make it absolute
    if (!imageSrc.startsWith('http') && !imageSrc.startsWith('data:')) {
      imageSrc = `http://localhost:8080${imageSrc}`;
    }
    imageHtml = `<div class="modal-field">
        <span class="modal-field-label">Image</span>
        <div class="modal-field-value">
          <img src="${imageSrc}" alt="${escapeHtml(sighting.species || 'Animal')}" class="sighting-modal-image" onerror="this.parentElement.parentElement.style.display='none'" />
        </div>
      </div>`;
  }
  
  modalBody.innerHTML = `
    ${imageHtml}
    <div class="modal-field">
      <span class="modal-field-label">Species</span>
      <div class="modal-field-value">${escapeHtml(sighting.species || 'Unknown')}</div>
    </div>
    <div class="modal-field">
      <span class="modal-field-label">Location</span>
      <div class="modal-field-value">${escapeHtml(sighting.location || 'Unknown')}</div>
    </div>
    <div class="modal-field">
      <span class="modal-field-label">Date</span>
      <div class="modal-field-value">${date}</div>
    </div>
    <div class="modal-field">
      <span class="modal-field-label">Time</span>
      <div class="modal-field-value">${time}</div>
    </div>
    <div class="modal-field">
      <span class="modal-field-label">Reported by</span>
      <div class="modal-field-value">${escapeHtml(displayName)}</div>
    </div>
    <div class="modal-field">
      <span class="modal-field-label">Description</span>
      <div class="modal-field-value description">${escapeHtml(sighting.description || 'No description provided.')}</div>
    </div>
  `;
  
  modal.classList.add('show');
}

// Close modal
function closeModal() {
  const modal = document.getElementById('sightingModal');
  modal.classList.remove('show');
}

// Close modal when clicking outside
window.onclick = function(event) {
  const modal = document.getElementById('sightingModal');
  if (event.target === modal) {
    closeModal();
  }
  
  // Close search suggestions/history when clicking outside
  const searchInput = document.getElementById('searchInput');
  const suggestions = document.getElementById('searchSuggestions');
  const history = document.getElementById('searchHistory');
  
  if (!searchInput.contains(event.target) && 
      !suggestions.contains(event.target) && 
      !history.contains(event.target)) {
    suggestions.classList.remove('show');
    history.classList.remove('show');
  }
}

// Setup search functionality
function setupSearch() {
  const searchInput = document.getElementById('searchInput');
  const suggestions = document.getElementById('searchSuggestions');
  const history = document.getElementById('searchHistory');
  
  // Show history on focus if input is empty
  searchInput.addEventListener('focus', () => {
    if (searchInput.value === '' && searchHistory.length > 0) {
      displaySearchHistory();
    }
  });
  
  // Handle input
  searchInput.addEventListener('input', (e) => {
    const query = e.target.value.trim();
    
    if (query === '') {
      suggestions.classList.remove('show');
      if (searchHistory.length > 0) {
        displaySearchHistory();
      }
      return;
    }
    
    history.classList.remove('show');
    performSearch(query);
  });
  
  // Handle search on Enter
  searchInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      const query = searchInput.value.trim();
      if (query) {
        performSearch(query);
        addToSearchHistory(query);
        suggestions.classList.remove('show');
      }
    }
  });
}

// Perform search
async function performSearch(query) {
  try {
    const response = await fetch(`${API_BASE}/search?query=${encodeURIComponent(query)}`);
    if (!response.ok) {
      throw new Error('Search failed');
    }
    
    const results = await response.json();
    filteredSightings = results;
    
    displayMapMarkers(results);
    displaySightingsGrid(results);
    displaySearchSuggestions(query, results);
    
  } catch (error) {
    console.error('Search error:', error);
    // Fallback to client-side search
    performClientSearch(query);
  }
}

// Client-side search fallback
function performClientSearch(query) {
  const lowerQuery = query.toLowerCase();
  
  filteredSightings = allSightings.filter(sighting => {
    const species = (sighting.species || '').toLowerCase();
    const location = (sighting.location || '').toLowerCase();
    const description = (sighting.description || '').toLowerCase();
    const displayName = (sighting.displayName || '').toLowerCase();
    
    return species.includes(lowerQuery) ||
           location.includes(lowerQuery) ||
           description.includes(lowerQuery) ||
           displayName.includes(lowerQuery);
  });
  
  displayMapMarkers(filteredSightings);
  displaySightingsGrid(filteredSightings);
  displaySearchSuggestions(query, filteredSightings);
}

// Display search suggestions
function displaySearchSuggestions(query, results) {
  const suggestions = document.getElementById('searchSuggestions');
  
  if (results.length === 0) {
    suggestions.innerHTML = '<div class="suggestion-item">No results found</div>';
    suggestions.classList.add('show');
    return;
  }
  
  // Get unique suggestions
  const uniqueSuggestions = new Set();
  results.slice(0, 5).forEach(sighting => {
    if (sighting.species) uniqueSuggestions.add(sighting.species);
    if (sighting.location) uniqueSuggestions.add(sighting.location);
  });
  
  suggestions.innerHTML = Array.from(uniqueSuggestions).map(item => 
    `<div class="suggestion-item" onclick="selectSuggestion('${item}')">${item}</div>`
  ).join('');
  
  suggestions.classList.add('show');
}

// Select suggestion
function selectSuggestion(text) {
  const searchInput = document.getElementById('searchInput');
  searchInput.value = text;
  performSearch(text);
  addToSearchHistory(text);
  document.getElementById('searchSuggestions').classList.remove('show');
  document.getElementById('searchHistory').classList.remove('show');
  searchInput.focus();
}

// Search history
function loadSearchHistory() {
  const saved = localStorage.getItem('searchHistory');
  if (saved) {
    searchHistory = JSON.parse(saved);
  }
}

function saveSearchHistory() {
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory));
}

function addToSearchHistory(query) {
  // Remove if already exists
  searchHistory = searchHistory.filter(q => q !== query);
  // Add to beginning
  searchHistory.unshift(query);
  // Keep only last 10
  searchHistory = searchHistory.slice(0, 10);
  saveSearchHistory();
}

function displaySearchHistory() {
  const history = document.getElementById('searchHistory');
  
  // Recommended queries based on common species/locations
  const recommendedQueries = ['Red-tailed Hawk', 'Coyote', 'Squirrel', 'Trousdale Parkway', 'Alumni Park'];
  
  let html = '';
  
  if (searchHistory.length > 0) {
    html += `
      <div class="history-header">Recent Searches</div>
      ${searchHistory.map(item => `
        <div class="history-item">
          <span class="history-item-text" onclick="selectSuggestion('${escapeHtml(item)}')">${escapeHtml(item)}</span>
          <button class="history-item-delete" onclick="removeFromHistory('${escapeHtml(item)}'); event.stopPropagation();">
            <span class="material-symbols-rounded" style="font-size: 18px;">close</span>
          </button>
        </div>
      `).join('')}
    `;
  }
  
  // Add recommended queries
  html += `
    <div class="history-header">Recommended</div>
    ${recommendedQueries.map(item => `
      <div class="history-item">
        <span class="history-item-text" onclick="selectSuggestion('${escapeHtml(item)}')">${escapeHtml(item)}</span>
      </div>
    `).join('')}
  `;
  
  history.innerHTML = html;
  history.classList.add('show');
}

function removeFromHistory(item) {
  searchHistory = searchHistory.filter(q => q !== item);
  saveSearchHistory();
  displaySearchHistory();
}

// Show error message
function showError(message) {
  console.error(message);
  // You can add a toast notification here if needed
}

// Make functions available globally
window.showSightingDetails = showSightingDetails;
window.showSightingDetailsById = showSightingDetailsById;
window.closeModal = closeModal;
window.selectSuggestion = selectSuggestion;
window.removeFromHistory = removeFromHistory;

