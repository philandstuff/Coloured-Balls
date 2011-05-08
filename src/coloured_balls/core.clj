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
  {:x 50 :y 200 :red 255 :blue 0 :green 0 :radius 30})

(def ball (atom (conj (make-ball) {:x-velocity 15 :y-velocity 10})))

(defn vert-bounce [ball]
  (conj ball {:y-velocity (* (- 0.8) (:y-velocity ball))}))

(defn horiz-bounce [ball]
  (conj ball {:x-velocity (* (- 0.9) (:x-velocity ball))}))

(defn move [ball]
  (let [old-x (:x ball)
	old-y (:y ball)
	delta-x (:x-velocity ball)
	delta-y (:y-velocity ball)
	delta-delta-y 5]
       (conj {:x (+ old-x delta-x) :y (+ old-y delta-y) :y-velocity (+ delta-y delta-delta-y)} (dissoc ball :x :y :y-velocity))
       ))

(defn hit-ground? [ball]
  (or
   (< (:y ball) 0)
   (> (:y ball) 400)))

(defn hit-wall? [ball]
  (or
   (< (:x ball) 0)
   (> (:x ball) 400)))

(defn bounce [ball]
  (let [new-ball
	(cond
	 (hit-wall? ball) (horiz-bounce ball)
	 (hit-ground? ball) (vert-bounce ball)
	 :else ball)]
    (move new-ball)))

(defn draw
  "Example usage of with-translation and with-rotation."
  []
  (background-float 0)
  (draw-ball @ball)
  (swap! ball bounce)
  )

(defn setup []
  "Runs once."
  (smooth)
  (no-stroke)
  (fill 226)
  (framerate 20))

;; Now we just need to define an applet:

(defapplet balls :title "Coloured balls"
  :setup setup :draw draw :size [400 400])

(defn -main [& args]
 (run balls true))

