(ns event-shuffle-clj.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [event-shuffle-clj.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/hello/John"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello, John"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
