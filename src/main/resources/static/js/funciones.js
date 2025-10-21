document.querySelector('.errorText').style.display = 'none';

document.getElementById('ButtonLogin').addEventListener('click', function(event) {
    var checkbox = document.getElementById('CheckBoxLegal');
    if (!checkbox.checked) {
        event.preventDefault();
        document.querySelector('.errorText').style.display = 'block';
    }

    
});
