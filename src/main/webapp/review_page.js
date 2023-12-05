window.addEventListener('load', function() {
    console.log('Page and all resources have finished loading');
    fetchAndDisplayTags();
});

function fetchAndDisplayTags() {
	let matched_id = getUrlParameter("matched_id");
    fetch("EssayServlet?action=displayTags&matched_id=" + matched_id, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
			}
		}) 
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); 
        })
        .then(data => {
			console.log("tags: "+data);
            document.getElementById('tag-display').innerText = data;
        })
        .catch(error => {
            console.error('There was a problem with fetching the string:', error);
        });
}

function submitFeedback() {
    var formData = new FormData();
    var file = document.querySelector('input[type="file"]');
    var feedback = file.files[0];
    formData.append('feedbackfile', feedback);
    formData.append('matched_id', getUrlParameter("matched_id"));
    console.log("matched_id: "+getUrlParameter("matched_id"));

    fetch("FeedbackServlet", {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(data => {
        console.log('Response from servlet:', data);
        window.location.href = "dashboard.html";
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
    });
}

function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    let regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    let results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
}

function downloadEssay() {
    let matched_id = getUrlParameter("matched_id")
    console.log("inserting matched_id: "+matched_id);
    fetch("EssayServlet?matched_id="+matched_id, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/pdf' 
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.blob();
    })
    .then(blob => {
        const url = URL.createObjectURL(blob);
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
