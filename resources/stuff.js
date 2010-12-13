$(document).ready( function() {
if (document.body.__defineGetter__) {
 
   if (HTMLElement) {
 
        var element = HTMLElement.prototype;
 
        if (element.__defineSetter__) {
                element.__defineSetter__("outerHTML",
                  function (html) {  
                         var el = document.createElement('div');
                         el.innerHTML = html;
                         var range = document.createRange();
                         range.selectNodeContents(el);
                         var documentFragment = range.extractContents();
                         this.parentNode.insertBefore(documentFragment, this);
                         this.parentNode.removeChild(this);
                  }
                );
            }
        if (element.__defineGetter__) {
 
            element.__defineGetter__("outerHTML",
 
                function () {
 
                     var parent = this.parentNode;
                     var el = document.createElement(parent.tagName);
                     el.appendChild(this);
                     var shtml = el.innerHTML;
                     parent.appendChild(this);
                     return shtml;
                  }
            );
     }
 
   }
}
 $("#document").contextMenu({
		menu: 'myMenu'
                
	},
	    function(action, el, pos) {
                showMenuAndUpdate(action,el,pos);
                $("#response").val($("#document").html());
 
                });
 
 
   function showMenuAndUpdate(action, el, pos) {
                if (action == "erase") {
                     var range = getTextRange();
                     var par =
                     range.commonAncestorContainer.parentElement ?
                     range.commonAncestorContainer.parentElement :
                     range.commonAncestorContainer.parentNode ?
                     range.commonAncestorContainer.parentNode : null;
                     if (par.nodeName == "SPAN" && par.firstElementChild==null) {
                         par.outerHTML = par.innerHTML;
                     }
                     else if (par.firstElementChild.nodeName == "SPAN") {
                        var text = document.createTextNode(par.firstElementChild.innerHTML);
                        par.replaceChild(text, par.childNodes[1]);
                        //par.insertChild(text,1);
                     }
                }
                else {
 
                var range = getTextRange();
                start = range.startContainer.parentNode ?
                    range.startContainer.parentNode :
                    range.startContainer.parentElement ?
                    range.startContainer.parentElement : null;
                end = range.endContainer.parentNode ?
                    range.endContainer.parentNode :
                    range.endContainer.parentElement ?
                    range.endContainer.parentElement : null;
                if (start.nodeName != "SPAN" && end.nodeName != "SPAN") {
                    var contents = range.extractContents();
                    var span = document.createElement("span");
                    span.setAttribute("class",action);
                    span.setAttribute("id","selection");
                    span.appendChild(contents);
                    range.insertNode(span);
                }
           }
 
   }
 
var submitted = 0;
function formValidation(form) {
    if (submitted) {
        alert("Form already submitted, please be patient");
        return false;}
 
    removeChildNodes($("#validation")[0]);
 
 
    if ($("#document")[0].getElementsByTagName("span").length < 4) {
        var par = document.createElement("p");
        par.appendChild(document.createTextNode("Not enough highlights in Document 1!"));
        $("#validation").append(par);
    }
 
    if ($("#validation")[0].children.length > 0) {
        var par = document.createElement("p");
        par.appendChild(document.createTextNode("Please complete the task before submitting."));
        $("#validation").append(par);
        return false;
    }
 
 
    if (!submitted) {
        form.annotationFinished.disabled=true;
        submitted = 1;
        form.submit();
    } 
}
 
function removeChildNodes(ctrl)
{
 
  while (ctrl.childNodes && ctrl.childNodes[0])
  {
    ctrl.removeChild(ctrl.childNodes[0]);
  }
}
 
 
function getTextRange() {
    if (window.getSelection) {
        return window.getSelection().getRangeAt(0);
    }
    else if (document.selection) {
        return document.selection.createRange();
    }
    return nil;
}
 
function getSelectedText() {
    if (window.getSelection) {
        return window.getSelection();
    }
    else if (document.selection) {
        return document.selection.createRange().text;
    }
    return '';
}
// Given the ID of the assignmentId form field element, populate it
// with the assignmentId parameter from the URL.  If no assignment ID
// is present, inform the worker that the HIT is being previewed.
function populateAssignmentID(field_id) {
  var assignment_id_field = document.getElementById(field_id);
  var paramstr = window.location.search.substring(1);
  var parampairs = paramstr.split("&");
  for (i in parampairs) {
    var pair = parampairs[i].split("=");
    if (pair[0] == "assignmentId") {
      if (pair[1] == "ASSIGNMENT_ID_NOT_AVAILABLE") {
        document.getElementById('previewnotice').innerHTML =
          "<p><b>You are previewing this HIT.</b>  To perform this HIT, please accept it.</p>";
      } else {
        assignment_id_field.value = pair[1];
      }
      return;
    }
  }
}
