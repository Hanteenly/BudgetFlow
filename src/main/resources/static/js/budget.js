var categoriesCache = [];
function findCategoryIdByName(name) {
    for (var i = 0; i < categoriesCache.length; i++) {
        if (categoriesCache[i].name === name) {
            return categoriesCache[i].id;
        }
    }
    return null;
}

function createBudget(){
    var period = document.getElementById("period").value.slice(0, 7);
    var categoryId = findCategoryIdByName(document.getElementById("category").value);
    fetch("/api/budget", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Basic " +
                btoa(
                    sessionStorage.getItem("name")
                    + ":" +
                    sessionStorage.getItem("password")
                )
        },
        body: JSON.stringify({
            category: {
                id: categoryId
            },
            limit: document.getElementById("limit").value,
            period: period
        })
    }).then(function (response){
        if(response.ok){
            loadBudget();
        }else{
            alert("Error: " + response.status);
            window.location.href = "/index.html";
        }
    })
}

function loadBudget(){
    fetch("/api/budget", {
        headers: {
            "Authorization": "Basic " +
                btoa(
                    sessionStorage.getItem("name")
                    + ":" +
                    sessionStorage.getItem("password")
                )
        }
    }).then(function (response) {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            alert("This name or password not found");
            window.location.href = "/index.html";
        } else {
            alert("Error: " + response.status);
            window.location.href = "/index.html";
        }
    }).then(function (budgets){
        if(budgets){
            document.getElementById("budgets").textContent = "";
            for(let i = 0; i < budgets.length; i++){
                const budget = budgets[i];
                const box = document.getElementById("budgets");
                box.textContent = box.textContent + budget.category.name + " " + budget.limit + " " + budget.period + "\n";
            }
        }
    })
}
function load_categoryes() {
    fetch("/api/category", {
        headers: {
            "Authorization": "Basic " + btoa(
                sessionStorage.getItem("name") + ":" + sessionStorage.getItem("password")
            )
        }
    }).then(function (response) {
        if (response.ok) {
            return response.json();
        }
    }).then(function (categoryes) {
        if (categoryes) {
            categoriesCache = categoryes;
        }
    });
}

function loadBudgetsSummary(){
    var auth = {
        "Authorization": "Basic " + btoa(
            sessionStorage.getItem("name") + ":" + sessionStorage.getItem("password")
        )
    };
    fetch("/api/budget", {
        headers: auth
    }).then(function (response) {
        if (response.ok) {
            return response.json();
        }
    }).then(function (budgets) {
        var box = document.getElementById("budgetSummary");
        box.textContent = "";
        if (!budgets) {
            return;
        }
        for (var i = 0; i < budgets.length; i++) {
            fetch("/api/budget/" + budgets[i].id + "/summary", {
                headers: auth
            }).then(function (response) {
                if (response.ok) {
                    return response.json();
                }
            }).then(function (summary) {
                if (summary) {
                    box.textContent = box.textContent
                        + summary.category.name + " "
                        + summary.limit + " "
                        + summary.spent + " "
                        + summary.remaining + " "
                        + summary.overLimit + "\n";
                }
            });
        }
    });
}
load_categoryes();
document.getElementById("register-button").addEventListener("click", function (event){
    event.preventDefault();
    createBudget();
});

document.getElementById("Summary").addEventListener("click", function (event){
    event.preventDefault();
    loadBudgetsSummary();
});
function logout() {
    sessionStorage.clear();
    window.location.href = "/index.html";
}
document.getElementById("logout-button").addEventListener("click", logout);
loadBudget();
