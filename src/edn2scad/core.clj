(ns edn2scad.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [clojure.edn :as edn]))

(defn make-literal [obj]
  (cond
    (or (vector? obj) (seq? obj)) (str "[" (str/join ", " (map make-literal obj)) "]")
    (ratio? obj) (str (float obj))
    :else (str obj)))

(defn make-arglist [args]
  (->> args
       (map #(if (map? %)
               (str/join ", " (map (fn [[k v]] (str (name k) "=" (make-literal v))) %))
               (make-literal %)))
       (str/join ", ")))

(defn make-scad
  ([data] (make-scad data 0))
  ([data depth]
   (let [[f args & children] data]
     (if (not (keyword? f))
       (apply str (map #(make-scad % depth) data))
       (str
        (apply str (repeat depth "  "))
        (name f)
        (str "(" (make-arglist args) ")")
        (if (empty? children)
          ";\n"
          (str " {\n"
               (apply str (map #(make-scad % (inc depth)) children))
               (apply str (repeat depth "  "))
               "}\n")))))))

(def cli-options
  [["-f" "--file FILE" "File containing EDN to interpret"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [parsed (cli/parse-opts args cli-options)]
    (cond
      (:errors parsed) (println (first (:errors parsed)))
      (:help (:options parsed)) (println (:summary parsed))
      (:file (:options parsed)) (print (make-scad (edn/read-string (slurp (:file (:options parsed))))))
      :else (print (make-scad (edn/read-string (first (:arguments parsed))))))))

  ;; (println (make-scad (grid [100 100] [5 5 1] [3 1] 10)))
