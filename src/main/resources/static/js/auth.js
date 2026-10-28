function registerUser() {
    var status = document.getElementById("auth-status");
    var name = document.getElementById("register-name").value;
    var password = document.getElementById("register-password").value;
    status.textContent = "Registering...";
    fetch("/User", {
        method: "POST",
        credentials: "omit",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: name,
            password: password
        })
    }).then(function (response) {
        if (response.ok) {
            sessionStorage.setItem("name", name);
            sessionStorage.setItem("password", password);
            window.location.href = "/account.html";
        } else if (response.status === 409) {
            status.textContent = "This name is already taken";
        } else {
            status.textContent = "Error: " + response.status;
        }
    }).catch(function (error) {
        status.textContent = "Request failed: " + error;
    });
}

function loginUser() {
    var status = document.getElementById("auth-status");
    var name = document.getElementById("login-name").value;
    var password = document.getElementById("login-password").value;
    status.textContent = "Logging in...";
    fetch("/api/accounts", {
        headers: {
            "Authorization": "Basic " + btoa(name + ":" + password)
        }
    }).then(function (response) {
        if (response.ok) {
            sessionStorage.setItem("name", name);
            sessionStorage.setItem("password", password);
            window.location.href = "/account.html";
        } else if (response.status === 401) {
            status.textContent = "This name or password not found";
        } else {
            status.textContent = "Error: " + response.status;
        }
    }).catch(function (error) {
        status.textContent = "Request failed: " + error;
    });
}

document.getElementById("register-button").addEventListener("click", registerUser);
document.getElementById("login-button").addEventListener("click", loginUser);
