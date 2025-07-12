// Modern login animation and validation (JS puro)
document.addEventListener('DOMContentLoaded', function() {
    var card = document.querySelector('.modern-login-card');
    if(card) {
        card.style.opacity = 0;
        card.style.transform = 'translateY(40px)';
        setTimeout(function() {
            card.style.transition = 'opacity 0.7s, transform 0.7s';
            card.style.opacity = 1;
            card.style.transform = 'translateY(0)';
        }, 100);
    }
    var form = document.querySelector('.modern-login-form');
    if(form) {
        form.addEventListener('submit', function(e) {
            var valid = true;
            var inputs = form.querySelectorAll('.modern-login-input');
            inputs.forEach(function(input) {
                if (!input.value) {
                    input.classList.add('invalid-login-input');
                    valid = false;
                } else {
                    input.classList.remove('invalid-login-input');
                }
            });
            if (!valid) {
                e.preventDefault();
            }
        });
        inputs.forEach(function(input) {
            input.addEventListener('input', function() {
                if (input.value) {
                    input.classList.remove('invalid-login-input');
                }
            });
        });
    }
});
// Estilo para input inválido
// Modern login animation and validation
$(document).ready(function() {
    $('.modern-card').hide().fadeIn(700);
    $('.modern-form').on('submit', function(e) {
        var valid = true;
        $(this).find('.modern-input').each(function() {
            if (!$(this).val()) {
                $(this).addClass('is-invalid');
                valid = false;
            } else {
                $(this).removeClass('is-invalid');
            }
        });
        if (!valid) {
            e.preventDefault();
        }
    });
    $('.modern-input').on('input', function() {
        if ($(this).val()) {
            $(this).removeClass('is-invalid');
        }
    });
});
