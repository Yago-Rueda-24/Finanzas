document.getElementById('terms').style.display = 'none';
document.getElementById('sucess_text').style.display = 'none';
document.getElementById('error').style.display = 'none';

document.getElementById('ButtonLogin').addEventListener('click', function (event) {

    document.getElementById('terms').style.display = 'none';
    document.getElementById('sucess_text').style.display = 'none';
    document.getElementById('error').style.display = 'none';

    var checkbox = document.getElementById('CheckBoxLegal');
    event.preventDefault();
    if (!checkbox.checked) {

        document.querySelector('.errorText').style.display = 'block';
    } else {

        var username = document.getElementById('text').value;
        var password = document.getElementById('password').value;

        if(username === "" || password === "") {
            document.getElementById('error').innerText = "El nombre de usuario y la contraseña no pueden estar vacíos.";
            document.getElementById('error').style.display = 'block';
            return;
        }

        registerUser(username, password);
    }
});

async function registerUser(username, password) {
    try {
        const response = await fetch('http://localhost:8080/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Error desconocido');
        }

        document.getElementById('sucess_text').style.display = 'block';
        console.log('Registro exitoso:', data);

    } catch (error) {
        console.error('Hubo un problema:', error.message);
        document.getElementById('error').innerText = error.message;
        document.getElementById('error').style.display = 'block';
    }
}
