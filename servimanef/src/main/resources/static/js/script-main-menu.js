// Modern main menu animation and interaction
function logout() {
    window.location.href = '/intranet';
}
document.addEventListener('DOMContentLoaded', function() {
    var options = document.querySelectorAll('.modern-menu-option');
    options.forEach(function(option) {
        option.style.opacity = 0;
        option.style.transform = 'translateY(40px)';
    });
    setTimeout(function() {
        options.forEach(function(option, i) {
            setTimeout(function() {
                option.style.transition = 'opacity 0.7s, transform 0.7s';
                option.style.opacity = 1;
                option.style.transform = 'translateY(0)';
            }, i * 120);
        });
    }, 100);
});
