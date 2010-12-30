(ns text-ann.make-annotations
  (:gen-class :name text_ann.MakeAnnotations)
  (:require
   [text-ann [core :as core]]
   [clojure.contrib [json :as json]
                    [duck-streams :as ds]
                    [command-line :as cli]]))

(defn -main [& args]
  (cli/with-command-line args
"text_ann.MakeAnnotations data-file-list template categories-file
data-file-list: file with paths to input text documents
template: HTML annotation template
categories-file: file with one category per-line

Output files are written to same location as corresponding data
file with out-ext (default \".ann\")"
    [[colors-file "file with one color per-line"]
     [out-ext "extension of output files" ".html"]
     args]
    (let [[data-file-list template categories-file & _] args
	  template (slurp template)
	  categories (cons "erase" (ds/read-lines categories-file))
	  colors (cons "white" (if colors-file (ds/read-lines colors-file)  core/colors))]
      (doseq [f (ds/read-lines data-file-list)
	      :let [out-name (str f out-ext)
		    doc (slurp f)]]
       (spit out-name
	     (core/instantiate-template
	      (merge (core/build-js-elem categories colors)
		     {:document doc})
	      template))))))