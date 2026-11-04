var authMode = "login";

function showAuthMode(mode) {
    authMode = mode;
    var title = document.getElementById("auth-title");
    var button = document.getElementById("auth-button");
    var switchLink = document.getElementById("auth-switch");
    var status = document.getElementById("auth-status");
    status.textContent = "";
    if (mode === "register") {
        title.textContent = "Register";
        button.textContent = "Register";
        switchLink.textContent = "Already have an account? Log in";
    } else {
        title.textContent = "Log in";
        button.textContent = "Log in";
        switchLink.textContent = "No account? Register";
    }
}

function registerUser() {
    var status = document.getElementById("auth-status");
    var name = document.getElementById("auth-name").value;
    var password = document.getElementById("auth-password").value;
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
    var name = document.getElementById("auth-name").value;
    var password = document.getElementById("auth-password").value;
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

function submitAuth() {
    if (authMode === "register") {
        registerUser();
    } else {
        loginUser();
    }
}

document.getElementById("auth-form").addEventListener("submit", function (event) {
    event.preventDefault();
    submitAuth();
});

document.getElementById("auth-switch").addEventListener("click", function (event) {
    event.preventDefault();
    if (authMode === "login") {
        showAuthMode("register");
    } else {
        showAuthMode("login");
    }
});
