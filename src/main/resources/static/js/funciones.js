document.getElementById('terms').style.display = 'none';
document.getElementById('sucess_text').style.display = 'none';
document.getElementById('error').style.display = 'none';
document.getElementById('div_APIKEY').style.display = 'none';

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

        if (username === "" || password === "") {
            document.getElementById('error').innerText = "El nombre de usuario y la contraseña no pueden estar vacíos.";
            document.getElementById('error').style.display = 'block';
            return;
        }

        registerUser(username, password);
    }
});

async function registerUser(username, password) {
    try {
        const response = await fetch('/auth/register', {
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


document.getElementById('ButtonGenerate').addEventListener('click', function (event) {
    event.preventDefault();
    console.log("Generando API Key...");

    document.getElementById('error_generate').style.display = 'none';
    document.getElementById('div_APIKEY').style.display = 'none';

    var username = document.getElementById('text_generate').value;
    var password = document.getElementById('password_generate').value;

    if (username === "" || password === "") {
        document.getElementById('error_generate').innerText = "El nombre de usuario y la contraseña no pueden estar vacíos.";
        document.getElementById('error_generate').style.display = 'block';
        return;
    }

    generateKey(username, password);

});

async function generateKey(username, password) {
    try {
        const response = await fetch('/auth/generate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        

        if (!response.ok) {
            const data = await response.json();
            throw new Error(data.error || 'Error desconocido');
        }
        const data = await response.text();
        console.log("API Key generada:", data);

        document.getElementById('generate_text').innerText = data;
        document.getElementById('div_APIKEY').style.display = 'flex';
        console.log('Registro exitoso:', data);

    } catch (error) {
        console.error('Hubo un problema:', error.message);
        document.getElementById('error_generate').innerText = error.message;
        document.getElementById('error_generate').style.display = 'block';
    }
}