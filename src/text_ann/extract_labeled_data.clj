(ns text-ann.extract-labeled-data
  (:gen-class
    :name text_ann.ExtractLabeledData)
  (:import [edu.umass.nlp.io IOUtils]
           [edu.umass.nlp.text HTMLUtils])
  (:use [mochi.nlp.process.doc-preprocess :only [process]])
  (:require [clojure.xml :as xml]
	    [clojure.contrib.command-line :as cli]
	    [clojure.contrib.str-utils :as su]
	    [clojure.zip :as zip]
	    [mochi.span :as span]
	    [clojure.contrib.zip-filter :as zf]
	    [clojure.contrib.zip-filter.xml :as zfx]
	    [clojure.contrib.duck-streams :as ds])
  (:import [mochi.nlp.process.tokenizer Token]))

(defn compute-offset [html start]
  (let [prefix (.substring html 0 start)
	pruned (.replaceAll prefix "<.*?>" "")
	delta  (- (.length prefix) (.length pruned))]
    delta))

(defn compute-labeled-spans [html]
  (let [span-elems (filter
		    #(= (.getName %) "span")
		    (.getAllElements (net.htmlparser.jericho.Source. html)))]
    (for [span-elem span-elems
	  :let [content-span (HTMLUtils/getContentSpan span-elem)
		s (.getStart content-span)
		t (.getStop content-span)
		offset (compute-offset html s)]]
      [(.getAttributeValue span-elem "class")
       (- s offset)
       (- t offset)])))

(defn- find-label [tok labeled-spans]
  (ffirst
   (filter
    (fn [[l s t]]
      (and (:abs-char-span tok)
	   (span/contains? [s t] (:abs-char-span tok))))
    labeled-spans)))

(defn- pad-start-stop [sent]
  (assoc sent
    :toks
    (concat [{:raw-word "<s>"}]
	    (:toks sent)
	    [{:raw-word "</s>"}])))

(defn process-file [f out]
  (let [html (ds/slurp* f)
	labeled-spans (compute-labeled-spans html)]
    (ds/write-lines out    
      (for [tok  (concat
		   [{:raw-word "<s>"}]
		   (mapcat :toks (process (.replaceAll html "<.*?>" "")))
		   [{:raw-word "</s>"}])
	    :let [label (find-label tok labeled-spans)]]
	(format "%s\t%s\t%d\t%d"
		(:raw-word tok)
		(cond
		 label label
		 (= (:raw-word tok) "<s>") "<s>"
		 (= (:raw-word tok) "</s>") "</s>"
		 :default "NONE")
		(or (first (:abs-char-span tok)) 0)
		(or (second (:abs-char-span tok)) 0))))))
             
(defn -main [& args]
  (cli/with-command-line args
"text_ann.ExtractLabeledData -- input-file-list
input-file-list has a single path per-line and
takes each of those annotated files and makes a token file
for use with text_ann.TrainModel. The default
is to write the token file to the same directory
as the input file with the out-ext option extension
default is \".tok \" "
    ;;  Options        
    [[out-ext "Extension for output files" ".tok"]     
    ;;  Main
     args]
    (let [[input-file-list out-dir & _] args]
      (doseq [f (ds/read-lines input-file-list)]
        (process-file f (str f out-ext))))))
