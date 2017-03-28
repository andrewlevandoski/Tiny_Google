//Change to edit number of suggestions
var MAX_SUGGESTIONS = 4;

(function() {
  $('input[type=text]').click(function() {
    $('input[type=file]').trigger('click');
  });

  $('input[type=file]').change(function() {
      $('input[type=text]').val($(this).val());
  });

  $('.matches').hide();

  // Get input and pass on to getJSON()
  $('.flexsearch-input').keyup(function(event) {
    var input = $(".flexsearch-input").val();
    $('.matches').html("");
    $('.matches').show();

    //short circuit if nothing in search box
    if(input.length == 0){
      return;
    }
    getJSON(input);
  });

  // Actual functionality
  function getJSON(input) {
    $.ajax({
      //Hack to get around CORS complaint in chrome :(
      //Makes suggestions slow as hell but only not server side fix for xml in ajax
      url:"https://crossorigin.me/https://www.google.com/complete/search?output=toolbar&q="+input,
      type:"GET",
      dataType:"XML"
    })

    .done(function(xml) {
      var count = 0; //limits number of suggestions
      $(xml).find('suggestion').each(function() {
        search = $(this).attr('data');
        search = search.toLowerCase();

        if (input.length > 0 && count < MAX_SUGGESTIONS) {
          $('.matches').append("<a target=\"_blank\" href=\"http://www.google.com/search?q=" + search + "\">" + search + "</a>");
          count++;
        }
      });
    })

    .fail(function() {
      console.log("Error");
    });
  }
})();
