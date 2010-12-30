(defproject text-ann "0.0.1-SNAPSHOT"
  :description "Tool for generating text annotation pages and running machine learning models
  on that text, and gathering its prediction on raw unannotated text. "
  :aot [text-ann.extract-labeled-data
	text-ann.make-annotations
	text-ann.train-model
	text-ann.make-features]
  :dependencies [[org.clojure/clojure "1.2.0"]
		 [edu.umass.nlp/umass-nlp "1.0-SNAPSHOT"]
		 [nlputil-clj "1.0-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]])
