firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // User is signed in.
    $(".login-cover").hide();
    var dialog = document.querySelector('#loginDialog');
    /*if (! dialog.showModal) {    dialogPolyfill.registerDialog(dialog);}*/
    dialog.close();	
	var database = firebase.database();
	var myUserRef = database.ref('USERS/' + user.uid );
	myUserRef.on('value', function(snapshot) {
		updateProfile( snapshot.val());
	});
	var usersRef = database.ref('USERS/');
	usersRef.orderByChild("name").on('value', function(snapshot) {
		$("#usersList").empty();
		snapshot.forEach(user => {				
			addUser(user.val());
		});
	});
  } else {
    $(".login-cover").show();
	// No user is signed in.
    var dialog = document.querySelector('#loginDialog');
    if (! dialog.showModal) {
      dialogPolyfill.registerDialog(dialog);
    }
    dialog.showModal();
	$("#loginProgress").hide();
	$("#loginBtn").show();
	$("#forgotBtn").show();
	$("#loginEmail").val("");
	$("#loginPassword").val("");
  }
});

function updateProfile(user){
	$("#profileName").text(user.name);
	$("#profileDepartment").text(user.department);
	$("#profileEmail").text(user.email);
	$("#profilePhone").text(user.phone);
	$("#profileSkype").text(user.skype);
	$("#profileBirthday").text($.datepicker.formatDate('dd M yy', new Date(user.birthday.time)));
	$("#profileFirst").text($.datepicker.formatDate('dd M yy', new Date(user.firstWorkingDate.time)));
	if (user.isAdmin){
			
	}
};

function addUser(user){	
	if(user.isAllowEditPastReports){
		$("#usersList").append('<li id=element"'+user.key+'" class="mdl-list__item"><span class="mdl-list__item-primary-content"> <i class="material-icons mdl-list__item-icon">person</i>'
		+ user.name + ' <button id="'+user.key+'" class="mdl-button mdl-js-button mdl-button--accent" onClick="onDisableClicked(this.id)">Disable Edit Mode</button></span></li>');
	} else{
		$("#usersList").append('<li id=element"'+user.key+'" class="mdl-list__item"><span class="mdl-list__item-primary-content"> <i class="material-icons mdl-list__item-icon">person</i>'
		+ user.name + ' <button id="'+user.key+'" class="mdl-button mdl-js-button mdl-button--primary"  onClick="onEnableClicked(this.id)">Enable Edit Mode</button></span></li>');
	}
};

function onEnableClicked(clicked_id){	
	firebase.database().ref('USERS/' + clicked_id +'/isAllowEditPastReports').set(true);
}

function onDisableClicked(clicked_id){
	firebase.database().ref('USERS/' + clicked_id +'/isAllowEditPastReports').set(false);
}

/* FORGOT PROCESS */

$("#forgotBtn").click(
  function(){
    var email = $("#loginEmail").val(); 
    if(email != ""){      
			$("#loginBtn").hide();
			$("#forgotBtn").hide();
			$("#loginProgress").show();
		firebase.auth().sendPasswordResetEmail(email)
		.then(function(){		
		  $("#loginError").show().text("Email sent");  
			$("#forgotBtn").show();
			$("#loginBtn").show();
		  $("#loginProgress").hide();
		})
		.catch(function(error) {			
			var errorMessage = error.message;
			$("#loginProgress").hide();
			$("#loginError").show().text(errorMessage);        
			$("#forgotBtn").show();
			$("#loginBtn").show();
		});		
	}
  }
);


/* LOGIN PROCESS */

$("#loginBtn").click(
  function(){
		var email = $("#loginEmail").val();
	var password = $("#loginPassword").val();
	if(email != "" && password != ""){
		$("#loginProgress").show();
		$("#loginBtn").hide();
		$("#forgotBtn").hide();
		firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {			
			var errorMessage = error.message;
			$("#loginError").show().text(errorMessage);
			$("#loginProgress").hide();
			$("#loginBtn").show();
			$("#forgotBtn").show();
		});
	}
});


/* LOGOUT PROCESS */

$("#signOutBtn").click(
  function(){
    firebase.auth().signOut().then(function() {      
    }).catch(function(error) {      
      alert(error.message);
    });
  }
);
