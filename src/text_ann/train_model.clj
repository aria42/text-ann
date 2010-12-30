(ns text-ann.train-model
  (:gen-class :name text_ann.TrainModel)
  (:import [edu.umass.nlp.ml.sequence CRF BasicLabelSeqDatum]
           [edu.umass.nlp.utils BasicPair]
           [edu.umass.nlp.io IOUtils]
           [edu.umass.nlp.exec Execution])
  (:require [clojure.contrib [command-line :as cli]       
	     [duck-streams :as ds]
	     [string :as str]]))

(defn train-crf [labeled-seqs sigma-squared num-iters]
  (doto (CRF.)
        (.setSigmaSquared sigma-squared)
        (.train labeled-seqs
          (let [opts (edu.umass.nlp.optimize.LBFGSMinimizer$Opts.)]
            (set! (.minIters opts) 100)
            (set! (.maxIters opts) 150)
            opts))))

(defn read-datum [lines]
  (let [all-fields (map #(seq (str/split #"\s+" %)) lines)]
    (BasicLabelSeqDatum.
     (map rest all-fields)
     (map first all-fields)
     1.0)))

(defn read-data [input-file-list]
  (for [f (ds/read-lines input-file-list)
	:let [ lines (ds/read-lines f) ]
	:when (not (empty? lines))]
    (read-datum lines)))       

(defn -main [& args]
  (Execution/init nil)
  (cli/with-command-line args
    "text_ann.TrainModel -- train-file-list out-model-file
Takes a list of feature files (from text_ann.MakeFeatures) as well
as an output path for model file."
    [[sigmaSquared "Sigma Squared" "0.5"]
     [numIters "Number of Iterations" "130"]
      args]
    (IOUtils/writeObject
      (train-crf (read-data (first args))
		 (Double/parseDouble sigmaSquared)
		 (Integer/parseInt numIters))
      (second args))))



