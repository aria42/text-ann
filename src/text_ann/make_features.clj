(ns text-ann.make-features
  (:gen-class :name text_ann.MakeFeatures)
  (:require [clojure.contrib.command-line :as cli]
	    [clojure.contrib.string :as str]
	    [clojure.contrib.duck-streams :as ds]))

(defn extract-word-features [w]
  (concat
   (filter identity
	   [(str "word=" w)
	    (when (.matches w "[A-Z].*") "hasInitCapital")
	    (when (.contains w "-") "contains-dash")
	    (when (.contains w ".") "contains-period")
	    (when (.matches w ".*[0-9].*") "hasDigit")
	    (str "wordShape="
		 (apply str
		    (map (fn [c]
			   (cond
			    (.matches (str c) "[A-Z]")  "X"
			    (.matches (str c) "[a-z]")  "x"
			    (.matches (str c) "[0-9]")  "d"
			    :default (str c)))
			 (seq w))))])
   (for [prefix-len (range 1 3)
	 :when (< prefix-len (.length w))]
     (format "prefix=%s" (.substring w 0 prefix-len)))
   (for [suffix-len (range 1 3)
	 :let [start (- (.length w) suffix-len)]
	 :when (>= start 0)]
     (format "suffix=%s" (.substring w start (.length w))))))

(defn extract-features [words i]
  (for [d (range -2 2)
	:let [index (+ i d)]
	:when (and (>= index 0) (< index (count words)))]
    (map
      (fn [f] (str f "&&pos=" d))
      (extract-word-features (nth words index)))))

(defn extract-all-features [words]
  (for [i (range (count words))]
    (cond
     (zero? i) [ ["<s>"]]
     (= i (dec (count words))) [["</s>"]]
     :default (extract-features words i))))

(defn- make-labeled-datum [words labels]
  (map
     (fn [label feats]
       (concat [label] feats))
     labels
     (extract-all-features words)))

(defn- process-file [f]
  (make-labeled-datum
   (map (fn [line]
	  (first (.split line "\\s+"))) (ds/read-lines f))
   (map (fn [line]
	  (second (.split line "\\s+"))) (ds/read-lines f))))

	    
(defn -main [& args]
  (cli/with-command-line args
"text_ann.MakeFeatures -- tok-file-list
Take a .tok file from ExtractLabeledData step and produces
a corresponding file in the same location where each line
corresponds to a token. The format of the line is a label
and then some number of cues or features. Each feature
is just an arbitrary string active for that given token. "
  [[out-ext "Extension for output feature file" ".feats"]
   args]
  (let [[tok-file-list & _] args]
    (doseq [f (ds/read-lines tok-file-list)]		  
      (spit (str f ".feats")
        (str/join "\n"
		  (map (fn [[label feats]]
			 (str label " "(str/join " " feats)))
		       (process-file f))))))))