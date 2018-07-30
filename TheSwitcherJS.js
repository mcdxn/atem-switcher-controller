function runTest1() {

    var result = app.javaCheckIsTheSwitcherWorking(1);

    if(result == "true")
      displayInsideTestResults("SUCCESS!");
    else
      displayInsideTestResults("FAIL!");

}

function runInit()
{
   
    disableSwitchButtons();

}

function connectToSwitcher(get_ip_address_from_element_id)
{
  var ipAddress = document.getElementById(get_ip_address_from_element_id).value;
  if(ipAddress.length > 15 || ipAddress.length == null)
   displayInsideConnectResults("Enter Valid IP Address");
  else 
   var connectResult = app.javaConnectToSwitcher(ipAddress);
  if(app.javaSwitcherIsConnected())
  {
      document.getElementById("btn_connectToSwitcher").disabled = true;
      document.getElementById("btn_enumerateInput").disabled = false;
      enableSwitchButtons();
  }
  
    displayInsideConnectResults(connectResult);
  
}

function enumerateInput()
{
    app.javaEnumerateInputArray();
    updateInputList();
}

function performSwitch(source,destination)
{
  var sourceSelected = document.getElementById(source).selectedIndex;
  var sourceValue = document.getElementById(source).options;
  
  var destinationSelected = document.getElementById(destination).selectedIndex;
  var destinationValue = document.getElementById(destination).options;
  
  app.javaPerformSwitchByCut(sourceValue[sourceSelected].value,destinationValue[destinationSelected].value);
}

function performSwitchByAuxSourceChange(source,destination)
{
  var sourceSelected = document.getElementById(source).selectedIndex;
  var sourceValue = document.getElementById(source).options;
  
  var destinationSelected = document.getElementById(destination).selectedIndex;
  var destinationValue = document.getElementById(destination).options;
  
  app.javaPerformSwitchBySetSource(sourceValue[sourceSelected].value,destinationValue[destinationSelected].value);
}

function performDualSwitch(src1,dest1,src2,dest2)
{
   performSwitchByAuxSourceChange(src1,dest1);
   performSwitchByAuxSourceChange(src2,dest2);
}

function enableSwitchButtons()
{
   var buttons = document.getElementsByName("btn_performSwitch");
 
    for(var x=0;x<buttons.length; x++) 
    { 
       buttons[x].disabled = false;
    }   
}

function disableSwitchButtons()
{
    var buttons = document.getElementsByName("btn_performSwitch");
 
    for(var x=0;x<buttons.length; x++) 
    { 
       buttons[x].disabled = true;
    }   
}

function updateInputList()
{
    var inputName = app.javaGetEnumeratedInputNamesArray();
    var inputPortType = app.javaGetEnumeratedInputPortTypesArray();
    var arrayLength = inputName.length;
    
    var inputList = document.getElementsByName("inputlist");
    var outputList = document.getElementsByName("outputlist");

    for(var x=0;x<inputList.length; x++) 
    { 
        var inputOption = [[]];
        var outputOption = [[]];
        for(var y=0;y<arrayLength;y++)
        {
            inputOption[0][y] = document.createElement("option");
            inputOption[0][y].textContent = inputName[y] + " " + inputPortType[y];
            inputOption[0][y].value = y;
            inputList[x].add(inputOption[0][y]);
        
            outputOption[0][y] = document.createElement("option");
            outputOption[0][y].textContent = inputName[y] + " " + inputPortType[y];
            outputOption[0][y].value = y;
            outputList[x].add(outputOption[0][y]);

        }
    }   
}

function displayInsideTestResults(result_to_display)
{
  var resultMessage = "Test Result: " + result_to_display;
  document.getElementById("test_results").innerHTML = resultMessage;
}

function displayInsideConnectResults(result_to_display)
{
  var resultMessage = "Connect Result: " + result_to_display;
  document.getElementById("connect_results").innerHTML = resultMessage;
}
