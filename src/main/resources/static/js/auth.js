
document.getElementById("register-form").addEventListener("submit", function (event) {
    event.preventDefault();
    fetch("/User", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: document.getElementById("register-name").value,
            password: document.getElementById("register-password").value
        })
    }).then(function (response) {
        if (response.ok) {
            alert("Registered");
            sessionStorage.setItem("name", document.getElementById("register-name").value);
            sessionStorage.setItem("password", document.getElementById("register-password").value);
            window.location.href = "/account.html";
        } else if (response.status === 409) {
            alert("This name is already taken");
        } else {
            alert("Error: " + response.status);
        }
    });
});

document.getElementById("login-form").addEventListener("submit", function (event) {
    event.preventDefault();
    fetch("/api/accounts", {
        headers: {
            "Authorization": "Basic " + btoa(
                document.getElementById("login-name").value
                + ":" +
                document.getElementById("login-password").value
            )
        }
    }).then(function (response) {
        if (response.ok) {
            alert("Go in");
            sessionStorage.setItem("name", document.getElementById("login-name").value);
            sessionStorage.setItem("password", document.getElementById("login-password").value);
            window.location.href = "/account.html";
        } else if (response.status === 401) {
            alert("This name or password not found");
        } else {
            alert("Error: " + response.status);
        }
    });
});
