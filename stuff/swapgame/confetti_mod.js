var ns = "http://www.w3.org/2000/svg"

var confettiContainer = document.createElementNS(ns, "foreignObject")
document.documentElement.appendChild(confettiContainer)
confettiContainer.setAttribute("x",     "-10000")
confettiContainer.setAttribute("y",     "-10000")
confettiContainer.setAttribute("width",  "20000")
confettiContainer.setAttribute("height", "20000")
confettiContainer.setAttribute("transform", "scale(0.01)")
confettiContainer.setAttribute("style", "pointer-events: none;")
confettiContainer.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml", "div"))
confettiContainer.children[0].setAttribute("style", `margin-top: 10000px; margin-left: 10000px; position: relative; width: 0px; height: 0px; overflow: visible;`)

// @ts-ignore
Array.prototype.choice = function () { return this[Math.floor(Math.random()*this.length)]; }
class Confetti {
	/**
	 * @param {number} x
	 * @param {number} y
	 */
	constructor(x, y) {
		this.pos = [x * 100, y * 100]
		// @ts-ignore
		this.color = ["red", "yellow", "lime", "blue"].choice()
		this.v = [(Math.random() - 0.5) * 10, (Math.random() - 0.5) * 10]
		this.dir = Math.random() * 360
		this.av = (Math.random() - 0.5) * 14
		this.size = Math.random() * 0.6
		requestAnimationFrame(() => this.tick())
		this.elm = document.createElementNS("http://www.w3.org/1999/xhtml", "div")
		this.elm.classList.add("confetti")
		confettiContainer.children[0].appendChild(this.elm)
		this.updateElm()
	}
	tick() {
		this.pos[0] += this.v[0]
		this.pos[1] += this.v[1]
		this.v[0] *= 0.99
		this.v[1] *= 0.99
		this.v[1] += 0.07
		this.size -= 0.004
		this.dir += this.av
		if (this.size <= 0) this.destroy()
		else {
			this.updateElm()
			requestAnimationFrame(() => this.tick())
		}
	}
	updateElm() {
		this.elm.setAttribute("style", `position: absolute; background: ${this.color}; top: ${this.pos[1]}px; left: ${this.pos[0]}px; width: ${this.size}em; height: ${this.size}em; transform: rotate(${this.dir}deg);`)
	}
	destroy() {
		this.elm.remove()
	}
}
export { Confetti as confetti }