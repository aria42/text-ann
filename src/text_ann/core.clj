(ns text-ann.core
  (:require [clojure.string :as clj-str]))

(defn- convert-key [k]
  (str "$$" (-> k
           str
           (.substring 1)
           .toUpperCase)
       "$$"))

(def colors
     ["orange"
      "#00CCFF"
      "#33FF33"
      "#FF6600"
      "#00FFFF"
      "#FFFF66"
      "#77FFFF"
      #_"#006600"
      "#FF77FF"
      "#77FF77"
      "#FFFF77"
      "brown"
      "green"
      "orange"
      "lime"
      "pink"])

(defn build-color-elems [cats colors]  
  (clj-str/join "\n"
   (map (partial format ".%s {background-color: %s;}")
        cats
        colors)))

(defn build-context-menu-elems [cats]
  (clj-str/join
   (map
    (fn [cat]
      (format "<li class=\"%s\"><a href=\"#%s\">%s</a></li>"
              cat cat cat))
    cats)))

(defn build-js-elem [cats colors]
  {:elems (build-color-elems cats colors)
   :menu (build-context-menu-elems cats)})

(defn instantiate-template [m template]
  (->> m
       (reduce
        (fn [res [k v]]
          (.replace res (convert-key k) v))
        template)))


