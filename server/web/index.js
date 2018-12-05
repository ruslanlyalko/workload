firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // User is signed in.
    $(".login-cover").hide();

    var dialog = document.querySelector('#loginDialog');
    /*
    if (! dialog.showModal) {
      dialogPolyfill.registerDialog(dialog);
    }
    */
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
	/*var usersRef = database.ref('USERS/');
	usersRef.orderByChild("name").on('child_changed', function(snapshot) {
		updateUser(snapshot.val());
	});*/
	
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
	$("#loginEmail").text("");
	$("#loginPassword").text("");
		
  }
});

function updateProfile(user){
	$("#userName").text(user.name);
	
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

function updateUser(user){
	$('#element'+user.key).empty();
	/*
	if(user.isAllowEditPastReports){
		$("#element"+user.key).add('<span class="mdl-list__item-primary-content"> <i class="material-icons mdl-list__item-icon">person</i>'
		+ user.name + ' <button id="'+user.key+'" class="mdl-button mdl-js-button mdl-button--accent" onClick="onDisableClicked(this.id)">Disable Edit Mode</button></span>');
	} else{
		$("#element"+user.key).add('<span class="mdl-list__item-primary-content"> <i class="material-icons mdl-list__item-icon">person</i>'
		+ user.name + ' <button id="'+user.key+'" class="mdl-button mdl-js-button mdl-button--primary"  onClick="onEnableClicked(this.id)">Enable Edit Mode</button></span>');
	}
	*/
};


function onEnableClicked(clicked_id){
	var database = firebase.database();
	 database.ref('USERS/' + clicked_id +'/isAllowEditPastReports').set(true);
}

function onDisableClicked(clicked_id){
	var database = firebase.database();
	database.ref('USERS/' + clicked_id +'/isAllowEditPastReports').set(false);
}

/* FORGOT PROCESS */

$("#aBtn").click(
  function(){

    var email = $("#loginEmail").val();    

    if(email != ""){      
		$("#aBtn").hide();
		$("#loginProgress").show();
		firebase.auth().sendPasswordResetEmail(email)
		.then(function(){		
		  $("#loginError").show().text("Email sent");  
		  $("#aBtn").show();
		  $("#loginProgress").hide();
		})
		.catch(function(error) {
			// Handle Errors here.
			var errorCode = error.code;
			var errorMessage = error.message;

			$("#loginProgress").hide();
			$("#loginError").show().text(errorMessage);        
			$("#aBtn").show();
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
      firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;

        $("#loginError").show().text(errorMessage);
        $("#loginProgress").hide();
        $("#loginBtn").show();
      });
    }
  }
);


/* LOGOUT PROCESS */

$("#signOutBtn").click(
  function(){

    firebase.auth().signOut().then(function() {
      // Sign-out successful.

    }).catch(function(error) {
      // An error happened.
      alert(error.message);
    });

  }
);
