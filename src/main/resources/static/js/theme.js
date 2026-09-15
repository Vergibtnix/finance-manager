(() => {
    const storageKey = 'finance-manager-theme';

    const getPreferredTheme = () => {
        const storedTheme = localStorage.getItem(storageKey);
        if (storedTheme === 'light' || storedTheme === 'dark') {
            return storedTheme;
        }

        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    };

    const applyTheme = (theme) => {
        document.documentElement.setAttribute('data-bs-theme', theme);

        const label = document.getElementById('theme-toggle-label');
        const toggle = document.getElementById('theme-toggle');
        const nextLabel = theme === 'dark' ? '☀️ Light Mode' : '🌙 Dark Mode';

        if (label) {
            label.textContent = nextLabel;
        }

        if (toggle) {
            toggle.setAttribute('aria-pressed', String(theme === 'dark'));
            toggle.setAttribute('title', nextLabel);
        }
    };

    const theme = getPreferredTheme();
    applyTheme(theme);

    document.addEventListener('DOMContentLoaded', () => {
        applyTheme(getPreferredTheme());

        const toggle = document.getElementById('theme-toggle');
        if (!toggle) {
            return;
        }

        toggle.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'dark' : 'light';
            const nextTheme = currentTheme === 'dark' ? 'light' : 'dark';
            localStorage.setItem(storageKey, nextTheme);
            applyTheme(nextTheme);
        });
    });
})();

