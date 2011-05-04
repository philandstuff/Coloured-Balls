(ns coloured-balls.core
  (:use [rosado.processing]
        [rosado.processing.applet]
	)
  (:gen-class))

;; here's a function which will be called by Processing's (PApplet)
;; draw method every frame. Place your code here. If you eval it
;; interactively, you can redefine it while the applet is running and
;; see effects immediately

(defn draw-ball [ball]
	(fill (:red ball) (:green ball) (:blue ball))
	(ellipse (:x ball) (:y ball) (:radius ball) (:radius ball)))

(defn make-ball []
  {:x (+ 25 (rand-int 350)) :y (+ 25 (rand-int 350)) :red (rand-int 256) :blue (rand-int 256) :green (rand-int 256) :radius (+ 1 (rand-int 70))})

(def ball (atom (conj (make-ball) {:x-velocity 15 :y-velocity 360})))

(defn vert-bounce [ball]
  (conj ball {:y-velocity (- (:y-velocity ball))}))

(defn horiz-bounce [ball]
  (conj ball {:x-velocity (- (:x-velocity ball))}))

(defn move [ball]
  (let [old-x (:x ball)
	old-y (:y ball)
	delta-x (:x-velocity ball)
	delta-y (:y-velocity ball)]
    (conj {:x (+ old-x delta-x) :y (+ old-y delta-y)} (dissoc ball :x :y))
    ))

(defn bounce [ball]
  (cond
   (< (:x ball) 0) (move (horiz-bounce ball))
   (> (:x ball) 400) (move (horiz-bounce ball))
   (< (:y ball) 0) (move (vert-bounce ball))
   (> (:y ball) 400) (move (vert-bounce ball))
   :else (move ball)))

(defn draw
  "Example usage of with-translation and with-rotation."
  []
  (background-float 125)
  (draw-ball @ball)
  (swap! ball bounce)
  )

(defn setup []
  "Runs once."
  (smooth)
  (no-stroke)
  (fill 226)
  (framerate 10))

;; Now we just need to define an applet:

(defapplet balls :title "Coloured balls"
  :setup setup :draw draw :size [400 400])

(defn -main [& args]
 (run balls true))

