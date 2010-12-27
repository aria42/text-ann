(defproject text-ann "0.0.1-SNAPSHOT"
  :description "Tool for generating text annotation pages. Focused on producing external HITs for Mechanical Turk."
  :aot [text-ann.extract-labeled-spans]
  :dependencies [[org.clojure/clojure "1.2.0"]
		 [edu.umass.nlp/umass-nlp "1.0-SNAPSHOT"]
		 [nlputil-clj "1.0-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]])
