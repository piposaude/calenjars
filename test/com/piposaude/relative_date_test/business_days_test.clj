(ns com.piposaude.relative-date-test.business-days-test
  (:require [clojure.test :refer :all]
            [com.piposaude.calenjars :refer [relative-date-add]]
            [tick.alpha.api :as t]))

(deftest should-calculate-correct-date-when-relative-date-add-date-with-business-days
  (are [days expected]
       (= expected (relative-date-add (t/date "2020-07-30") days :business-days))
    2 (t/date "2020-08-03")
    1 (t/date "2020-07-31")
    5 (t/date "2020-08-06")
    -5 (t/date "2020-07-23")))

(deftest should-calculate-correct-date-when-relative-date-add-date-time-with-business-days
  (are [days expected]
       (= expected (relative-date-add (t/date-time "2020-07-30T11:11:00") days :business-days))
    2 (t/date-time "2020-08-03T11:11:00")
    1 (t/date-time "2020-07-31T11:11:00")
    5 (t/date-time "2020-08-06T11:11:00")
    -5 (t/date-time "2020-07-23T11:11:00")))

(deftest should-calculate-correct-date-when-relative-date-add-saturday-date-with-business-days
  (are [days expected]
       (= expected (relative-date-add (t/date "2020-08-01") days :business-days))
    1 (t/date "2020-08-03")
    7 (t/date "2020-08-11")
    -1 (t/date "2020-07-31")
    -5 (t/date "2020-07-27")))

(deftest should-calculate-correct-date-when-relative-date-add-saturday-date-time-with-business-days
  (are [days expected]
       (= expected (relative-date-add (t/date-time "2020-08-01T22:15:09") days :business-days))
    1 (t/date-time "2020-08-03T22:15:09")
    7 (t/date-time "2020-08-11T22:15:09")
    -1 (t/date-time "2020-07-31T22:15:09")
    -5 (t/date-time "2020-07-27T22:15:09")))

(deftest should-calculate-correct-date-when-relative-date-add-sunday-date-with-business-days
  (are [days expected]
       (= expected (relative-date-add (t/date "2020-08-02") days :business-days))
    1 (t/date "2020-08-03")
    7 (t/date "2020-08-11")
    -1 (t/date "2020-07-31")
    -5 (t/date "2020-07-27")))

(deftest should-calculate-correct-date-when-relative-date-add-sunday-date-time-with-business-days
  (are [days expected]
       (= expected (relative-date-add (t/date-time "2020-08-02T03:15") days :business-days))
    1 (t/date-time "2020-08-03T03:15")
    7 (t/date-time "2020-08-11T03:15")
    -1 (t/date-time "2020-07-31T03:15")
    -5 (t/date-time "2020-07-27T03:15")))

(deftest should-go-to-next-business-day-or-stay-when-relative-date-add-zero-days-with-business-days
  (are [start-date expected-end-date]
       (= (t/date expected-end-date) (relative-date-add (t/date start-date) 0 :business-days))
    "2020-07-31" "2020-07-31"
    "2020-08-01" "2020-08-03"
    "2020-08-02" "2020-08-03"
    "2020-08-03" "2020-08-03"
    "2020-08-04" "2020-08-04"))

(deftest should-go-to-next-business-day-or-stay-when-relative-date-add-zero-days-date-time-with-business-days
  (are [start-date expected-end-date]
       (= (t/date-time (str expected-end-date "T03:15")) (relative-date-add (t/date-time (str start-date "T03:15")) 0 :business-days))
    "2020-07-31" "2020-07-31"
    "2020-08-01" "2020-08-03"
    "2020-08-02" "2020-08-03"
    "2020-08-03" "2020-08-03"
    "2020-08-04" "2020-08-04"))
