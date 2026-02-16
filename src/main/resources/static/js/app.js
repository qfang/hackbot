
(function() {
	
	var app = angular.module('qbot', []);
	
	app.controller("QbotController", function($scope) {

		// Function registry for safe command dispatch
		var commandHandlers = {
			'findTime': findTime
		};

		this.findAnswer = function(value){
			var ans = questions[value];			
			if (typeof ans == 'string' && ans.indexOf("$") == 0){
				var commandName = ans.substr(1);
				var handler = commandHandlers[commandName];
				if (handler) {
					return handler();
				}
			} 
			return ans;
		}
	
	});
	
	var questions = {
		"what is your name" : "my name is qbot",
		"what time is it" : "$findTime"
	};

	function findTime(){
		return "right now it's " + new Date();
	}	

})();


