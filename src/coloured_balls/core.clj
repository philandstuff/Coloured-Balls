(ns coloured-balls.core
  (:use [rosado.processing]
        [rosado.processing.applet]
	[coloured-balls.motion])
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

(def ball (conj (make-ball) {:velocity 15 :heading (rand-int 360)}))

(defn draw
  "Example usage of with-translation and with-rotation."
  []
  (def ball ball)
  (background-float 125)
  (draw-ball ball)
  (when (or 
	 (> (:y ball) (- 400 (/ (ball :radius) 2)))
	 (< (:y ball) (/ (ball :radius) 2)))
    (def ball (conj ball {:heading (- 360 (:heading ball)) :radius (+ 1 (:radius ball))}))
    )
  (when (or 
	 (> (:x ball) (- 400 (/ (ball :radius) 2)))
	 (< (:x ball) (/ (ball :radius) 2)))
    (def ball (conj ball {:heading (- 180 (:heading ball)) :radius (+ 1 (:radius ball))}))
    )
  (def ball (move ball))
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

