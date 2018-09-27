const functions = require('firebase-functions');
const moment = require('moment');
const admin = require('firebase-admin');
admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//

 
 function sendMessagesViaFCM(tokens, payload){
	if(tokens.length > 0)
		return  admin.messaging().sendToDevice(tokens, payload)
			.then(response => {
				console.log("Push Sent: ", response);
				return 0;
			})
			.catch(error => {
				console.log("Push Error: ", error);
			});			
	else 
		return console.log("Push No Tokens");
}

 exports.checkReports = functions.https.onRequest((request, response) => {   
	const dateStr = moment().format("YYYYMMDD"); 
	const hour = parseInt(moment().format("HH"))+3;	
	const hourStr = hour.toString();
	const reportsPromise = admin.database().ref("/REPORTS").once('value');
	const usersPromise = admin.database().ref("/USERS").once('value');
	const holidaysPromise = admin.database().ref("/HOLIDAYS").once('value');
  
	return Promise.all([reportsPromise, usersPromise, holidaysPromise]).then((result)=> {		
		const reportsSnap =result[0];
		const usersSnap =result[1];
		const holidaysSnap =result[2];
		
		if(holidaysSnap.child(dateStr).val()){
			console.log("Today is Holiday!" );			
			return response.send("Today is Holiday!");
		}
		
		var tokens = [];
		var count = 0;
		var missedCount = 0;
			
		usersSnap.forEach(user => {	
			var userObj = user.val();
			var aKey = dateStr + "_" + userObj.key;
			console.log("checking ", aKey);	
			if(reportsSnap.child(aKey).val()){ 
				count  = count + 1;
				console.log("found ", userObj.name);
			}else{
				missedCount = missedCount + 1;
				console.log("missed ", userObj.name + " (" +userObj.token+")");
				if(userObj.token ){				
					console.log(userObj.notificationHour, hourStr);		
					if(userObj.notificationHour === hourStr){
						tokens.push(userObj.token);
					}
				}
			}
		});
	
		var payload = {
			data:{
				title: "It's time to fill in the Workload",
				body: "It won't take more than one minute!"
			}			
		}	

		console.log("Reports Count : " + count + "; Missed Count " + missedCount);
		response.send("Reports Count : " + count + "; Missed Count " + missedCount);	
		
		return sendMessagesViaFCM(tokens, payload);		
	});  
 });

 exports.getProjectInfo = functions.https.onCall((data, context) => {
	// Checking that the user is authenticated.
	if (!context.auth) {	  
	  throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
		  'while authenticated.');
	}
	
	const project = data.project;
	const from = data.from;
	const to = data.to;
	
	// Checking attribute.
	if (!(typeof project === 'string') || project.length === 0) {	  
	  throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
		  'three arguments, first "project" should containing the project title.');
	}
	if (!(typeof from === 'number') || from < 0) {	  
	  throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
		  'three arguments, second "from" should containing the from date.');
	}
	if (!(typeof to === 'number') || to < 0) {	  
	  throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
		  'three arguments, third "to" should containing the to date.');
	}
	
	// results data
	var iOSCount = 0;
	var AndroidCount = 0;	
	var BackendCount = 0;
	var DesignCount = 0;
	var PMCount = 0;
	var QACount = 0;
	var OtherCount = 0;
	
	const reportsPromise = admin.database().ref("/REPORTS").once('value');
	
	return reportsPromise.then(reportsSnap => {
			
			reportsSnap.forEach(report => {	
				var reportObj = report.val();			
				if(reportObj.date.time >from && reportObj.date.time < to){
					if(reportObj.project1Time > 0 && reportObj.project1Name === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.project1Time;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.project1Time;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.project1Time;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.project1Time;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.project1Time;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.project1Time;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.project1Time;
					}
					
					if(reportObj.project2Time > 0 && reportObj.project2Name === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.project2Time;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.project2Time;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.project2Time;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.project2Time;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.project2Time;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.project2Time;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.project2Time;
					}
					
					if(reportObj.project3Time > 0 && reportObj.project3Name === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.project3Time;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.project3Time;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.project3Time;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.project3Time;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.project3Time;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.project3Time;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.project3Time;
					}
					
					if(reportObj.project4Time > 0 && reportObj.project4Name === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.project4Time;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.project4Time;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.project4Time;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.project4Time;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.project4Time;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.project4Time;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.project4Time;
					}
					
					
				}
			});	

			return {
				iOS: iOSCount,
				Android: AndroidCount,
				Backend: BackendCount,
				Design: DesignCount,
				PM: PMCount,
				QA: QACount,
				Other: OtherCount	
			}	
		});
	    
});