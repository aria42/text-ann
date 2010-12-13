(ns text-ann.core
  (:require [clojure.string :as clj-str]))

(defn- convert-key [k]
  (str "$$" (-> k
           str
           (.substring 1)
           .toUpperCase)
       "$$"))

(def colors
     ["#FF7777"
      "#77FFFF"
      "#FF77FF"
      "#77FF77"
      "#FFFF77"])

(defn build-color-elems [cats]  
  (clj-str/join "\n"
   (map (partial format ".%s {background-color: %s;}")
        cats
        colors)))

(defn build-context-menu-elems [cats]
  ;; <li class="food"><a href="#food">food</a></li>  -->
  (clj-str/join
   (map
    (fn [cat]
      (format "<li class=\"%s\"><a href=\"#%s\">%s</a></li>"
              cat cat cat))
    cats)))

(defn build-js-elem [cats]
  {:elems (build-color-elems cats)
   :menu (build-context-menu-elems cats)})

(defn instantiate-template [m template]
  (->> m
       (reduce
        (fn [res [k v]]
          (.replace res (convert-key k) v))
        template)))


