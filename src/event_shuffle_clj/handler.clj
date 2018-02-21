(ns event-shuffle-clj.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s])
  (:import (java.util UUID)))

(defn create-uuid! []
  (str (UUID/randomUUID)))

(s/defschema Event
  {:id    s/Str
   :name  s/Str
   :dates [s/Str]})

(s/defschema Vote
  {:name  s/Str
   :votes [s/Str]})

;; TODO replace with real db
(defonce database (atom []))

(defn add-event! [event]
  (let [new-event (assoc event :id (create-uuid!))]
    (swap! database conj new-event)
    (ok (:id new-event))))

(defn get-events []
  (ok @database))

(defn find-event [id]
  (ok (filter (fn [x]
                (= (get x :id) id)) @database)))

(defapi app
  (swagger-routes)
  (context "/api/v1/event" []
    (POST "/" []
      :summary "Creates a new event"
      :body [event Event]
      :return s/Str
      (add-event! event))
    (GET "/list" []
      :summary "Lists all events"
      :return [Event]
      (get-events))
    (context "/:id" [id]
      (GET "/" [id]
        :summary "Find an event by id"
        :return Event
        (find-event id))
      (GET "/results" []
        (ok {:message (str "Results for event " id)}))
      (POST "/vote" []
        :summary "Adds vote to the event"
        :body [vote Vote]
        :return s/Str
        (ok (str "Voted on event " id))))))

;; testing
(comment
  ;; clear
  (reset! database [])
  (println)
  (println (filter #(= (get % :id) "4334494d-1f2b-4e61-8508-b5be57d1d857")) @database)

  (println (filter #(= :id "4334494d-1f2b-4e61-8508-b5be57d1d857") @database))
  (println (add-event! {:name "Bob" :date "01-01-2010"}))
  (println (ok {:message @database}))
  (println (get-events))
  (println (find-event "4334494d-1f2b-4e61-8508-b5be57d1d857")))
