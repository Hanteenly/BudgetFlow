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
            var box = document.getElementById("budgets");
            box.textContent = "";
            for (var i = 0; i < budgets.length; i++) {
                var budget = budgets[i];
                var tile = document.createElement("div");
                tile.className = "tile";
                var title = document.createElement("strong");
                title.textContent = budget.category.name;
                tile.appendChild(title);
                var line = document.createElement("div");
                line.textContent = budget.limit + " · " + budget.period;
                tile.appendChild(line);
                box.appendChild(tile);
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
                    var tile = document.createElement("div");
                    tile.className = "tile";
                    var title = document.createElement("strong");
                    title.textContent = summary.category.name;
                    tile.appendChild(title);
                    var line = document.createElement("div");
                    line.textContent = "spent " + summary.spent + " / " + summary.limit;
                    tile.appendChild(line);
                    var meta = document.createElement("div");
                    meta.className = "muted";
                    meta.textContent = summary.overLimit
                        ? "over limit"
                        : "left " + summary.remaining;
                    tile.appendChild(meta);
                    box.appendChild(tile);
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

var summaryOpen = false;
document.getElementById("Summary").addEventListener("click", function (event){
    event.preventDefault();
    var box = document.getElementById("budgetSummary");
    if (summaryOpen) {
        box.classList.add("is-hidden");
        this.textContent = "Show summary";
        summaryOpen = false;
        return;
    }
    summaryOpen = true;
    box.classList.remove("is-hidden");
    this.textContent = "Hide summary";
    loadBudgetsSummary();
});
function logout() {
    sessionStorage.clear();
    window.location.href = "/index.html";
}
document.getElementById("logout-button").addEventListener("click", logout);
loadBudget();
