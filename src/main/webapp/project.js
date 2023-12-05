window.addEventListener('load', function() {
    console.log('Page and all resources have finished loading');
    let name = document.getElementById("nameinner");
    name.textContent = getCookie("username");
});

// used for storing project map values (courseName, essayID)
function setCookie(name, value, days) {
    const expires = new Date();
    expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
    document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/`;
}

// used for retrieving project map as cookie
function getCookie(name) {
    const cookieArray = document.cookie.split(';');
    for (const cookie of cookieArray) {
        const [cookieName, cookieValue] = cookie.split('=');
        if (cookieName.trim() === name) {
            return cookieValue;
        }
    }
    return null;
}

// retrieve proejct map if exists, 
// else create 
const storedProjectMap = getCookie('projectMap');
const projectMap = storedProjectMap ? new Map(JSON.parse(storedProjectMap)) : new Map();

// executes when submit button clicked
function start_project() {
    var formData = new FormData();

    var courseName = document.querySelector('input[name="course_name"]').value;
    formData.append('essay_name', courseName);
    
    var file = document.querySelector('input[type="file"]');
    var essay = file.files[0];
    formData.append('uploadessay', essay);

    const checkedCheckboxes = [];
    const checkboxNames = ['tag1', 'tag2', 'tag3', 'tag4', 'tag5', 'tag6', 'tag7', 'tag8'];
    
    // accounts for guest accounts that do not have 
    // access to tags 
    const notGuest = checkboxNames.every(name => !!document.getElementById(name));
    if (notGuest) {
        checkboxNames.forEach(name => {
            const checkbox = document.getElementById(name);
            if (checkbox.checked) {
                checkedCheckboxes.push(checkbox.value);
            }
        });
        const tags = checkedCheckboxes.join(', '); 
        formData.append('tags', tags);
    }

    // hit Essay Servlet API to post start project data 
    fetch("EssayServlet", {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(essayID => {
        // store EssayID in map for future reference
        console.log('Response from servlet:', essayID);
        window.location.href = "dashboard.html";
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
    });
}

function downloadEssay() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    
    // course name will be passed as a parameter 
    // when redirected from Dashboard to Feedback page
    const currCourseName = urlParams.get('course_name'); 
    
    // retrieve corresponding Essay ID from project map 
    // using course name as key 
    const essayID = projectMap.get(currCourseName);
    var formData = new FormData();
    formData.append(essayID);

    fetch("EssayServlet", {
        method: 'GET',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.blob();
    })
    .then(essay => {
        const url = URL.createObjectURL(essay);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'essay_to_review.pdf'; 
        document.body.appendChild(a);
        a.click();
        URL.revokeObjectURL(url);
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
    });
  }

// used to display tags for essay on review page 
function fetchAndDisplayTags() {
fetch('EssayServlet?action=displayTags') 
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json(); 
    })
    .then(data => {
        const tags = data.tags;
        document.getElementById('tag-display').innerText = tags;
    })
    .catch(error => {
        console.error('There was a problem with fetching the string:', error);
    });
}
