// Login fetch
document.getElementById('loginform').addEventListener('submit',function(event){
	console.log("in login form");
	event.preventDefault();
	const loginInfo = {
		username: document.getElementById('usernameL').value,
		password: document.getElementById('passwordL').value
	};
	fetch('LoginServlet', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		 body: JSON.stringify(loginInfo)
		 
	})
	.then(response => {
		if (response.ok){
			return response.json();
			
		} else {
			throw new Error('Request failed');
		}
	})
	.then(data => {
		const status = data.status;
		console.log(status);
		console.log(data.username);
		if (status === 'fail'){ // display login failed message
			var displayError = document.getElementById("loginerror");
			displayError.innerHTML = "Invalid username/password";
		} else { // send to home page
			console.log("sending to dashboard page");
			window.location.href = 'dashboard.html';
		}
	})
	.catch(error => {
  	console.error('Error:', error);
	})
});


// new user SignUp
document.getElementById('signupform').addEventListener('submit',function(event){
	console.log("in signup form");
	event.preventDefault();
	const SignUpInfo = {
		username: document.getElementById('usernameS').value,
		password: document.getElementById('passwordS').value,
		first: document.getElementById('fname').value,
		last: document.getElementById('lname').value
	};
	fetch('SignUpServlet', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		 body: JSON.stringify(SignUpInfo)
		 
	})
	.then(response => {
		if (response.ok){
			return response.json();
			
		} else {
			throw new Error('Request failed');
		}
	})
	.then(data => {
		const status = data.status;
		console.log(status);
		console.log(data.username);
		if (status === 'fail'){ // display login failed message
			var displayError = document.getElementById("signuperror");
			displayError.innerHTML = "Invalid username/password";
		} else { // send to home page
			console.log("sending to dashboard page");
			window.location.href = 'dashboard.html';
		}
	})
	.catch(error => {
  	console.error('Error:', error);
	})
});

document.getElementById('guestcontinue').addEventListener('submit',function(event){
	event.preventDefault();
	const SignUpInfo = {
		username: "guest",
		password: "guest",
		first: "guest",
		last: "guest"
	};
	fetch('SignUpServlet', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		 body: JSON.stringify(SignUpInfo)
		 
	})
	.then(response => {
		if (response.ok){
			return response.json();
			
		} else {
			throw new Error('Request failed');
		}
	})
	.then(data => {
		const status = data.status;
		console.log(status);
		console.log(data.username);
		if (status === 'fail'){ // display login failed message
			var displayError = document.getElementById("guesterror");
			displayError.innerHTML = "Invalid username/password";
		} else { // send to home page
			console.log("sending to dashboard page");
			window.location.href = 'dashboard.html';
		}
	})
	.catch(error => {
  	console.error('Error:', error);
	})
});