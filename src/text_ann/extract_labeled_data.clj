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
	    [clojure.contrib.duck-streams :as ds]))

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
      (span/contains? [s t] (:abs-char-span tok)))
    labeled-spans)))

(defn process-file [f out]
  (let [html (ds/slurp* f)
	labeled-spans (compute-labeled-spans html)]
    (ds/write-lines out    
      (for [tok (mapcat :toks (process (.replaceAll html "<.*?>" "")))
	    :let [label (find-label tok labeled-spans)]]
	(format "%s\t%s\t%d\t%d"
		(:raw-word tok)
		(or label "NONE")
		(first (:abs-char-span tok))
		(second (:abs-char-span tok)))))))
             
(defn -main [& args]
  (cli/with-command-line args
    "extract_labeled_spans -- input-file-list out-dir"
    ;;  Options        
    [[out-dir "Output Directory"]
     [out-ext "Extension for output files" ".raw-ann"]
    ;;  Main
     args]
    (let [[input-file-list out-dir & _] args]
      (println input-file-list)
      (doseq [f (ds/read-lines input-file-list)]
        (process-file f 
          (if out-dir 
            (-> (IOUtils/changeDir f out-dir) 
                (IOUtils/changeExt out-ext))
            (IOUtils/changeExt f out-ext)))))))
