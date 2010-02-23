function initMenu() {
  $('#navList ul').hide();
  
  //$('#navList ul').filter("strong").show();
  $('#navList ul').filter(function(index) {return $('strong', this).length == 1;}).show();


  $('#navList li a.parent').mouseover(
    function() {
      var checkElement = $(this).next();
      if((checkElement.is('ul')) && (checkElement.is(':visible'))) {
        return false;
        }
      if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
        $('#navList ul').filter(function(index) {return $("strong", this).length == 0}).slideUp('normal');
        checkElement.slideDown('normal');
        return false;
        }
      }
    );
  
    $('div').filter(function(index) {return $('#navList', this).length == 0;}).mouseover(
    function() {
      
        $('#navList ul').filter(function(index) {return $("strong", this).length == 0}).slideUp('normal');
        
      }
    );
    $('#navColumn').mouseleave(
    function() {
      
        $('#navList ul').filter(function(index) {return $("strong", this).length == 0}).slideUp('normal');
        
      }
    );
    
    $('li:parent').css('margin', '0px');
   
   
  }
$(document).ready(function() {initMenu();});