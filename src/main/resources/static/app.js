class ScoreboardApp {
    constructor() {
        this.baseUrl = window.location.origin + '/api/scoreboard';
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadScoreboard();
    }

    setupEventListeners() {
        // Form submission
        document.getElementById('scoreForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.addScore();
        });

        // Refresh button
        document.getElementById('refreshBtn').addEventListener('click', () => {
            this.loadScoreboard();
        });

        // Clear button
        document.getElementById('clearBtn').addEventListener('click', () => {
            this.clearScoreboard();
        });

        // Limit selector
        document.getElementById('limitSelect').addEventListener('change', () => {
            this.loadScoreboard();
        });
    }

    async addScore() {
        const playerName = document.getElementById('playerName').value.trim();
        const score = parseInt(document.getElementById('score').value);

        if (!playerName || isNaN(score)) {
            this.showError('Please enter valid player name and score');
            return;
        }

        try {
            const response = await fetch(`${this.baseUrl}/add`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    playerName: playerName,
                    score: score
                })
            });

            if (response.ok) {
                const newScore = await response.json();
                this.showSuccess(`Score added for ${newScore.playerName}: ${newScore.score}`);
                this.clearForm();
                this.loadScoreboard();
            } else {
                this.showError('Failed to add score');
            }
        } catch (error) {
            this.showError('Error adding score: ' + error.message);
        }
    }

    async loadScoreboard() {
        this.showLoading(true);
        this.hideError();

        try {
            const limitSelect = document.getElementById('limitSelect');
            const limit = limitSelect.value;

            let url = `${this.baseUrl}/all`;
            if (limit !== 'all') {
                url = `${this.baseUrl}/top/${limit}`;
            }

            const response = await fetch(url);

            if (response.ok) {
                const scores = await response.json();
                this.displayScores(scores);
            } else {
                this.showError('Failed to load scoreboard');
            }
        } catch (error) {
            this.showError('Error loading scoreboard: ' + error.message);
        } finally {
            this.showLoading(false);
        }
    }

    async clearScoreboard() {
        if (!confirm('Are you sure you want to clear all scores? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await fetch(`${this.baseUrl}/clear`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showSuccess('Scoreboard cleared successfully');
                this.loadScoreboard();
            } else {
                this.showError('Failed to clear scoreboard');
            }
        } catch (error) {
            this.showError('Error clearing scoreboard: ' + error.message);
        }
    }

    displayScores(scores) {
        const tbody = document.getElementById('scoreTableBody');
        tbody.innerHTML = '';

        if (scores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 40px; color: #666;">No scores yet. Be the first to add one!</td></tr>';
            return;
        }

        scores.forEach((score, index) => {
            const row = document.createElement('tr');
            const rank = index + 1;

            // Format timestamp
            const date = new Date(score.timestamp);
            const formattedDate = date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});

            // Add rank styling for top 3
            let rankClass = 'rank';
            let rankDisplay = rank;
            if (rank === 1) {
                rankDisplay = '🥇 1';
            } else if (rank === 2) {
                rankDisplay = '🥈 2';
            } else if (rank === 3) {
                rankDisplay = '🥉 3';
            }

            row.innerHTML = `
                <td class="${rankClass}">${rankDisplay}</td>
                <td><strong>${this.escapeHtml(score.playerName)}</strong></td>
                <td><span style="font-weight: bold; color: #667eea;">${score.score.toLocaleString()}</span></td>
                <td>${formattedDate}</td>
            `;

            tbody.appendChild(row);
        });
    }

    showLoading(show) {
        const loading = document.getElementById('loading');
        const table = document.getElementById('scoreTable');

        if (show) {
            loading.style.display = 'block';
            table.style.display = 'none';
        } else {
            loading.style.display = 'none';
            table.style.display = 'table';
        }
    }

    showError(message) {
        const errorDiv = document.getElementById('error');
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';

        // Auto-hide after 5 seconds
        setTimeout(() => {
            this.hideError();
        }, 5000);
    }

    hideError() {
        document.getElementById('error').style.display = 'none';
    }

    showSuccess(message) {
        // Remove existing success messages
        const existingSuccess = document.querySelector('.success');
        if (existingSuccess) {
            existingSuccess.remove();
        }

        // Create and show success message
        const successDiv = document.createElement('div');
        successDiv.className = 'success';
        successDiv.textContent = message;

        const form = document.getElementById('scoreForm');
        form.parentNode.insertBefore(successDiv, form);

        // Auto-hide after 3 seconds
        setTimeout(() => {
            successDiv.remove();
        }, 3000);
    }

    clearForm() {
        document.getElementById('playerName').value = '';
        document.getElementById('score').value = '';
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize the app when the DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ScoreboardApp();
});
