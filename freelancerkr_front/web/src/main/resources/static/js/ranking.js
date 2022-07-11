
	function init(element){
		kvArray = [{key:0, value:false},
				{key:1, value:false},
				{key:2, value:false}];

		var cardNode = findParentNode(element,3);
		kvArray.forEach(function(obj){
			// cardNode.querySelector('.ranking>div').setAttribute('hidden',true);
			cardNode.querySelector('.ranking>div:nth-child('+ ((+obj.key)+1) +')').setAttribute('hidden',true);
		});
		return kvArray;
	}

	function findParentNode(element, num) {
    var parent = element;
    for (var i = 1; i <= num; i++) {
        if (parent.parentNode) {
            parent = parent.parentNode;
        	}
    	}
    // parent.remove();
	return parent;
	}

	function cancelRanking(e){
		init(e);
		var btnContainer = findParentNode(e,2);

		btnContainer.previousElementSibling.removeAttribute('hidden');
		btnContainer.setAttribute('hidden',true);
		return;
	}

	function toggleRanking(e,ranking){
		// console.log("this",e);
		// console.log("this",e.parentElement);
		// console.log("this",e.parentElement.previousSibling);
		// console.log("this.parent.parent", findParentNode(e,3));
		var cardNode = findParentNode(e,3);
		var kvArray = init(e);
		var newArr = [];

		var setToggle = kvArray.map(function(obj, index){
			var currentKV = [];
			// console.log("The current index is ==>" + index);
			// console.log(obj.key === ranking ? true : false);

			obj.value = (obj.key === ranking ? true : false);

			newArr[index] = { key: obj.key, value: obj.value };

			// console.log("new objs" + index +  "is ..." + newArr[index] );
			return newArr[index];

		}, ranking);

		var toggle = function(){
			// console.log("newArr is ..." + setToggle);
			var btnContainer = findParentNode(e,2);

			setToggle.forEach(function(obj){

				if(obj.value === true){
					// alert(obj.key + "is... " + obj.value);
					cardNode.querySelector('.ranking>div:nth-child('+ ((+obj.key)+1) +')').removeAttribute('hidden');
					btnContainer.nextElementSibling.removeAttribute('hidden');
					btnContainer.setAttribute('hidden',true);
				}
			});
		}
		toggle.apply(null,setToggle);
	}
