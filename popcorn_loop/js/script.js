
document.addEventListener("DOMContentLoaded", function () {

    const page = location.pathname.split("/").pop();

    // Read and save data
    function read(key) {
        return JSON.parse(localStorage.getItem(key) || "[]");
    }

    function save(key, value) {
        localStorage.setItem(key, JSON.stringify(value));
    }
 
    // Get movie details
    function getMovie(card) {
        const title = card.querySelector("h3");
        const image = card.querySelector("img");
        const rating = card.querySelector(".rating, .rating-badge");
        const info = card.querySelector(".movie-card-body p");

        return {
            title: title ? title.textContent.trim() : "Movie",
            image: image ? image.getAttribute("src") : "",
            alt: image ? image.alt : "Movie poster",
            rating: rating ? rating.textContent.trim() : "",
            info: info ? info.textContent.trim() : ""
        };
    }

    // Show message
    function showMessage(parent, message) {
        let box = parent.querySelector(".js-message");

        if (!box) {
            box = document.createElement("p");
            box.className = "js-message";
            parent.appendChild(box);
        }

        box.textContent = message;
    }


    // ADD TO FAVORITES
    document.querySelectorAll(".movie-card").forEach(card => {

        if (page === "favorites.html") return;

        const body = card.querySelector(".movie-card-body");

        if (!body || body.querySelector(".js-favorite-btn")) return;

        const button = document.createElement("button");
        button.type = "button";
        button.className = "btn btn-primary js-favorite-btn";
        button.textContent = "♡ Add to Favorites";

        button.addEventListener("click", function () {

            const movie = getMovie(card);
            let favorites = read("popcornFavorites");
            let found = false;

            for (let i = 0; i < favorites.length; i++) {
                if (favorites[i].title === movie.title) {
                    found = true;
                }
            }

            if (found) {
                favorites = favorites.filter(item => item.title !== movie.title);
                button.textContent = "♡ Add to Favorites";
                showMessage(body, "Movie removed from favorites.");
            } else {
                favorites.push(movie);
                button.textContent = "♥ Added";
                showMessage(body, "Movie added to favorites!");
            }

            save("popcornFavorites", favorites);
        });

        body.appendChild(button);
    });


    // SEARCH, FILTER AND SORT MOVIES
    if (page === "movies.html") {

        const bar = document.querySelector(".filter-bar");
        const grid = document.querySelector(".movie-grid");

        if (bar && grid) {

            const search = bar.querySelector('input[type="text"]');
            const selects = bar.querySelectorAll("select");

            const genre = selects[0];
            const language = selects[1];
            const sort = selects[2];

            const cards = Array.from(grid.querySelectorAll(".movie-card"));

            function filterMovies() {

                const term = search.value.toLowerCase();

                // Search and filter
                cards.forEach(card => {

                    const movie = getMovie(card);
                    const text = (movie.title + " " + movie.info).toLowerCase();

                    const genreText = movie.info.toLowerCase();
                    const genreValue = genre.value.toLowerCase();

                    const genreMatch = !genreValue ||
                        genreText.includes(genreValue) ||
                        (genreValue === "scifi" && genreText.includes("sci-fi"));

                    const languageText = (card.dataset.language || "english").toLowerCase();

                    const languageMatch = !language.value ||
                        languageText === language.value;

                    if (text.includes(term) && genreMatch && languageMatch) {
                        card.style.display = "";
                    } else {
                        card.style.display = "none";
                    }
                });

                // Get visible movies
                const visible = cards.filter(card => card.style.display !== "none");

                // Sort movies
                visible.sort((a, b) => {

                    const A = getMovie(a);
                    const B = getMovie(b);

                    if (sort.value === "name-asc") {
                        return A.title.localeCompare(B.title);
                    }

                    if (sort.value === "name-desc") {
                        return B.title.localeCompare(A.title);
                    }

                    if (sort.value === "rating-high") {
                        return parseFloat(B.rating) - parseFloat(A.rating);
                    }

                    if (sort.value === "year-new") {
                        const yearA = parseInt(A.info.match(/\d{4}/)?.[0] || "0");
                        const yearB = parseInt(B.info.match(/\d{4}/)?.[0] || "0");

                        return yearB - yearA;
                    }

                    return 0;
                });

                visible.forEach(card => grid.appendChild(card));
            }

            if (search && genre && language && sort) {
                search.addEventListener("input", filterMovies);
                genre.addEventListener("change", filterMovies);
                language.addEventListener("change", filterMovies);
                sort.addEventListener("change", filterMovies);
            }
        }
    }


    // FAVORITES PAGE
    if (page === "favorites.html") {

        const grid = document.querySelector(".movie-grid");

        if (grid) {

            function renderFavorites() {

                const favorites = read("popcornFavorites");
                grid.innerHTML = "";

                if (favorites.length === 0) {
                    grid.textContent = "Your watchlist is empty. Add movies from the Movies page!";
                    return;
                }

                favorites.forEach(movie => {

                    const card = document.createElement("div");
                    card.className = "movie-card";

                    const img = document.createElement("img");
                    img.src = movie.image;
                    img.alt = movie.alt || movie.title;

                    const body = document.createElement("div");
                    body.className = "movie-card-body";

                    const title = document.createElement("h3");
                    title.textContent = movie.title;

                    const rating = document.createElement("span");
                    rating.className = "rating";
                    rating.textContent = movie.rating;

                    const info = document.createElement("p");
                    info.textContent = movie.info;

                    const remove = document.createElement("button");
                    remove.type = "button";
                    remove.className = "btn btn-primary";
                    remove.textContent = "Remove";

                    remove.addEventListener("click", function () {

                        const favorites = read("popcornFavorites").filter(
                            item => item.title !== movie.title
                        );

                        save("popcornFavorites", favorites);
                        renderFavorites();
                    });

                    body.append(title, rating, info, remove);
                    card.append(img, body);
                    grid.appendChild(card);
                });
            }

            renderFavorites();
        }
    }


    // REVIEWS PAGE
    if (page === "reviews.html") {

        const form = document.querySelector("form.form-container");

        function renderReviews() {

            const section = Array.from(document.querySelectorAll(".section")).find(
                s => s.querySelector(".section-title") &&
                s.querySelector(".section-title").textContent.toLowerCase().includes("recent reviews")
            );

            if (!section) return;

            section.querySelectorAll(".js-user-review").forEach(el => el.remove());

            const reviews = read("popcornReviews");

            reviews.forEach(item => {

                const card = document.createElement("div");
                card.className = "review-card js-user-review";

                const h = document.createElement("h4");
                h.textContent = item.name;

                const meta = document.createElement("p");
                meta.textContent = "Movie: " + item.movie +
                    " | Rating: " + item.rating + "/10";

                const p = document.createElement("p");
                p.textContent = item.review;

                card.append(h, meta, p);
                section.appendChild(card);
            });
        }

        if (form) {

            form.addEventListener("submit", function (event) {

                event.preventDefault();

                const name = document.getElementById("userName").value.trim();
                const email = document.getElementById("userEmail").value.trim();
                const rating = document.getElementById("userRating").value;
                const review = document.getElementById("userReview").value.trim();
                const movie = form.querySelector("select").value;

                if (!name || !email || !rating || !review || !movie) {
                    alert("Please fill in all fields.");
                    return;
                }

                if (Number(rating) < 1 || Number(rating) > 10) {
                    alert("Rating must be between 1 and 10.");
                    return;
                }

                const reviews = read("popcornReviews");

                reviews.unshift({ name, movie, rating, review });
                save("popcornReviews", reviews);

                renderReviews();
                form.reset();

                alert("Thank you! Your review has been submitted.");
            });
        }

        renderReviews();
    }


    // CONTACT PAGE
    if (page === "contact.html") {

        const form = document.querySelector("form.form-container");

        if (form) {

            form.addEventListener("submit", function (event) {

                event.preventDefault();

                const inputs = form.querySelectorAll("input, textarea");

                for (let i = 0; i < inputs.length; i++) {
                    if (inputs[i].value.trim() === "") {
                        alert("Please fill in all fields.");
                        return;
                    }
                }

                alert("Thank you! Your message has been received.");
                form.reset();
            });
        }
    }


    // HOME PAGE GENRE LINKS
    if (page === "index.html" || page === "") 
    {
        document.querySelectorAll(".genre-tag").forEach(link => 
        {
            link.addEventListener("click", function (event) 
            {
                const value = this.textContent.trim().toLowerCase();
                const supported = ["action", "drama", "comedy", "sci-fi", "adventure"];
                if (supported.includes(value))
                {
                    event.preventDefault();
                    location.href = "movies.html?genre=" + encodeURIComponent(value);
                }
            });
        });
    }


    // SELECT GENRE FROM URL
    if (page === "movies.html") 
    {
        const requestedGenre = new URLSearchParams(location.search).get("genre");
        const genreSelect = document.querySelectorAll(".filter-bar select")[0];
        if (requestedGenre && genreSelect) 
        {
            const value = requestedGenre === "sci-fi" ? "scifi" : requestedGenre;
            for (let i = 0; i < genreSelect.options.length; i++) 
            {
                if (genreSelect.options[i].value === value) 
                {
                    genreSelect.value = value;
                    genreSelect.dispatchEvent(new Event("change"));
                    break;
                }
            }
        }
    }

});
