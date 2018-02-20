(ns event-shuffle-clj.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(defn create-uuid! []
  (str (java.util.UUID/randomUUID)))

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
    (ok new-event)))

(defn get-events []
  (ok @database))

(defn find-event [id]
  (ok ((filter #(= (:id id))) @database)))

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

  (println (add-event! {:name "Bob" :date "01-01-2010"}))
  (println (ok {:message @database}))
  (println (get-events))
  (println (find-event "7cbaf159-0f31-4a2b-8dd4-d26fbc92bfac")))
