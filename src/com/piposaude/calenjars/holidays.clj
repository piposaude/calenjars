(ns com.piposaude.calenjars.holidays
  (:require [clojure.edn :as edn]
            [instaparse.core :as insta]
            [com.piposaude.calenjars.common :as common]
            [com.piposaude.calenjars.check :as check]
            [com.piposaude.calenjars.types.ddmmm :as ddmm]
            [com.piposaude.calenjars.types.ddmmmyyyy :as ddmmyyyy]
            [com.piposaude.calenjars.types.expression :as expression]
            [com.piposaude.calenjars.types.nth-day-of-week :as nth-day-of-week]
            [com.piposaude.calenjars.constants :refer :all]))

(defn valid-year? [year]
  (and (integer? year) (<= MIN-YEAR year MAX-YEAR)))

(defn parse-holiday-opts [[_ & opts]]
  (reduce (fn [parsed-opts [opt-kw & args]]
            (case opt-kw
              :observed
              (assoc parsed-opts :observed? true)

              :start-year
              (assoc parsed-opts :start-year (edn/read-string (first args)))

              parsed-opts)) {} opts))

(defn get-holiday [year [_ name [type & args] opts]]
  (let [{:keys [observed? start-year]} (parse-holiday-opts opts)]
    (condp = type
      :ddmmm (ddmm/get-holiday-ddmm year name args observed? start-year)
      :ddmmmyyyy (ddmmyyyy/get-holiday-ddmmyyyy year name args)
      :nth-day-of-week (nth-day-of-week/get-holiday-nth-day-of-week year name args start-year)
      :expression (expression/get-holiday-by-expression year name (first args))
      nil)))

(defn remove-exceptions [holidays]
  (let [exception-dates (set (map :date (filter :exception? holidays)))]
    (remove #(some #{(:date %)} exception-dates) holidays)))

(defn- holidays-for-year-with-exception-key [year holiday-file]
  (cond
    (not (valid-year? year))
    (throw (ex-info (str "Invalid year: " year) {:year year}))

    (not (check/valid-holiday-file? holiday-file))
    (throw (ex-info (str "Invalid holiday file: " holiday-file) {:holiday-file holiday-file :errors (check/get-errors holiday-file)}))

    :default
    (let [parser (insta/parser (clojure.java.io/resource PARSER-GRAMMAR-FILENAME))
          result (parser (slurp holiday-file))
          holidays (conj
                    (keep #(get-holiday year %) (common/drop-include result))
                    (when (common/holiday-was-included? result)
                      (holidays-for-year-with-exception-key year (common/included-filename holiday-file (second (first result))))))]
      (-> (remove nil? holidays)
          flatten
          remove-exceptions))))

(defn holidays-for-year [year holiday-file]
  (map #(select-keys % [:name :date]) (holidays-for-year-with-exception-key year holiday-file)))
