(ns coloured-balls.core
  (:use [rosado.processing]
        [rosado.processing.applet]
	)
  (:gen-class))

;; here's a function which will be called by Processing's (PApplet)
;; draw method every frame. Place your code here. If you eval it
;; interactively, you can redefine it while the applet is running and
;; see effects immediately

(def x-size 400)
(def y-size 400)

(defn flip [y-coord]
  (- y-size y-coord))

(defn draw-ball [ball]
	(fill (:red ball) (:green ball) (:blue ball))
	(ellipse (:x ball) (flip (:y ball)) (:radius ball) (:radius ball)))

(defn make-ball []
  {:x 50 :y 200 :red 255 :blue 0 :green 0 :radius 30})

(def ball (atom (conj (make-ball) {:x-velocity 5 :y-velocity 5})))

(defn vert-bounce [ball]
  (conj ball {:y-velocity (* (- 0.8) (:y-velocity ball))}))

(defn horiz-bounce [ball]
  (conj ball {:x-velocity (* (- 1) (:x-velocity ball))}))

(defn move [ball]
  (let [x (:x ball)
	y (:y ball)
	dx (:x-velocity ball)
	dy (:y-velocity ball)]
       (conj {:x (+ x dx) :y (+ y dy)} (dissoc ball :x :y))
       ))

(def default-gravity -2)

(defn accelerate [ball]
  (let [v (:y-velocity ball)
	dv default-gravity]
    (conj {:y-velocity (+ v dv)} (dissoc ball :y-velocity))))

(defn hit-ground? [ball]
  (let [y (:y ball),
	dy (:y-velocity ball) radius (:radius ball)]
    (and
     (<= y radius)
     (< dy 0))))

(defn hit-wall? [ball]
  (let [x (:x ball) radius (:radius ball)] (or
   (< x radius)
   (> x (- x-size radius)))))

(defn bounce [ball]
  (let [new-ball
	(cond
	 (hit-wall? ball) (horiz-bounce ball)
	 (hit-ground? ball) (vert-bounce ball)
	 :else ball)]
    (accelerate (move new-ball))))

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
  :setup setup :draw draw :size [x-size y-size])

(defn -main [& args]
 (run balls true))

