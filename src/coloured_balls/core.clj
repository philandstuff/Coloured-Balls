(ns coloured-balls.core
  (:use [rosado.processing]
        [rosado.processing.applet]
	[coloured-balls.collision]
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
	(ellipse (:x ball) (flip (:y ball)) (* 2 (:radius ball)) (* 2 (:radius ball))))

(defn make-ball []
  {:x (rand-int 200) :y (+ 200 (rand-int 200)) :red (+ 100 (rand-int 155)) :blue 0 :green 0 :radius 15 :x-velocity 5 :y-velocity 5})

(defn make-balls [number]
  (repeatedly number make-ball))

(def balls (atom (make-balls 2)))

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
     (<= y (* 2 radius))
     (< dy 0))))

(defn hit-wall? [ball]
  (let [x (:x ball) radius (:radius ball)] (or
   (< x (* 2 radius))
   (> x (- x-size radius)))))

(defn bounce-walls [ball]
  (let [new-ball
	(cond
	 (hit-wall? ball) (horiz-bounce ball)
	 (hit-ground? ball) (vert-bounce ball)
	 :else ball)]
    (accelerate (move new-ball))))

(defn bounce [ball-1 ball-2]
  (map (comp vert-bounce horiz-bounce) [ball-1 ball-2]))

(defn hit? [ball-1 ball-2]
  (< (distance ball-1 ball-2)
     (+ (:radius ball-1) (:radius ball-2))))

(defn average-key [a b k]
  (/ (+ (k a) (k b)) 2))

(defn merge-ball [ball-1 ball-2]
  (conj (into {} (map (fn [[k _]] [k (average-key ball-1 ball-2 k)]) ball-1))
	{:radius (+ (:radius ball-1) (:radius ball-2))}))

(defn merge-balls [balls]
  (if (= 1 (count balls))
    balls
    (let [[ball-1 ball-2] balls]
      (if (hit? ball-1 ball-2)
	[(merge-ball ball-1 ball-2)]
	[ball-1 ball-2]))))

(defn transform-balls [balls]
  (map bounce-walls (merge-balls balls)))

(defn draw
  "Example usage of with-translation and with-rotation."
  []
  (background-float 0)
  (doseq [ball @balls] (draw-ball ball))
  (swap! balls transform-balls))

(defn setup []
  "Runs once."
  (smooth)
  (no-stroke)
  (fill 226)
  (framerate 20))

;; Now we just need to define an applet:

(defapplet app-balls :title "Coloured balls"
  :setup setup :draw draw :size [x-size y-size])

(defn -main [& args]
  (run app-balls true))